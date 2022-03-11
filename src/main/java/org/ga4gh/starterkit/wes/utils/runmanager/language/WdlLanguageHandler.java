package org.ga4gh.starterkit.wes.utils.runmanager.language;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.ga4gh.starterkit.wes.config.language.LanguageConfig;
import org.ga4gh.starterkit.wes.config.language.WdlLanguageConfig;
import org.ga4gh.starterkit.wes.model.RunLog;
import org.ga4gh.starterkit.wes.model.RunStatus;
import org.ga4gh.starterkit.wes.model.State;
import org.ga4gh.starterkit.wes.model.WesLog;
import org.ga4gh.starterkit.wes.model.WesRun;
import org.ga4gh.starterkit.wes.utils.hibernate.WesHibernateUtil;
import org.ga4gh.starterkit.wes.utils.runmanager.language.wdl.CromwellRunStatus;
import org.ga4gh.starterkit.wes.utils.runmanager.language.wdl.CromwellStatus;
import org.ga4gh.starterkit.wes.utils.runmanager.language.wdl.CromwellTaskMetadata;
import org.ga4gh.starterkit.wes.utils.runmanager.language.wdl.CromwellWorkflowMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class WdlLanguageHandler extends AbstractLanguageHandler {

    @Autowired
    private WesHibernateUtil hibernateUtil;

    private WdlLanguageConfig languageConfig;

    private static final Map<CromwellStatus, State> CROMWELL_STATE_MAP = new HashMap<>() {{
        put(CromwellStatus.Submitted, State.INITIALIZING);
        put(CromwellStatus.Succeeded, State.COMPLETE);
    }};

    private static final String CROMWELL_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    public void setup() {}

    @Override
    public void setLanguageConfig(LanguageConfig languageConfig) {
        this.languageConfig = (WdlLanguageConfig) languageConfig;
    }

    @Override
    public WdlLanguageConfig getLanguageConfig() {
        return languageConfig;
    }

    /* ##################################################
       # SUBMIT WORKFLOW RUN
       ################################################## */
    
    public String[] constructWorkflowRunCommand() throws Exception {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String cromwellSubmitUrl = getLanguageConfig().getCromwellBaseUrl() + "api/workflows/v1";
        String workflowUrl = getWesRun().getWorkflowUrl();

        HttpPost postRequest = new HttpPost(cromwellSubmitUrl);
        HttpEntity httpEntity = MultipartEntityBuilder
            .create()
            .addTextBody("workflowUrl", workflowUrl)
            .build();
        postRequest.setEntity(httpEntity);
        CloseableHttpResponse response = httpClient.execute(postRequest);

        byte[] responseBodyBytes = response.getEntity().getContent().readAllBytes();
        String responseBodyString = new String(responseBodyBytes, StandardCharsets.UTF_8);
        ObjectMapper mapper = new ObjectMapper();
        CromwellRunStatus cromwellRunStatus = mapper.readValue(responseBodyString, CromwellRunStatus.class);
        WesRun wesRun = getWesRun();
        wesRun.setCromwellRunId(cromwellRunStatus.getId());
        getHibernateUtil().updateEntityObject(WesRun.class, wesRun.getId(), wesRun);
        
        return null;
    }

    /* ##################################################
       # GET RUN STATUS
       ################################################## */
    
    public RunStatus determineRunStatus() throws Exception {
        RunStatus runStatus = new RunStatus(getWesRun().getId(), State.UNKNOWN);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String cromwellStatusUrl = getLanguageConfig().getCromwellBaseUrl()
            + "api/workflows/v1/"
            + getWesRun().getCromwellRunId()
            + "/status";
        
        HttpGet getRequest = new HttpGet(cromwellStatusUrl);
        CloseableHttpResponse response = httpClient.execute(getRequest);
        byte[] responseBodyBytes = response.getEntity().getContent().readAllBytes();
        String responseBodyString = new String(responseBodyBytes, StandardCharsets.UTF_8);
        ObjectMapper mapper = new ObjectMapper();
        CromwellRunStatus cromwellRunStatus = mapper.readValue(responseBodyString, CromwellRunStatus.class);
        State wesState = WdlLanguageHandler.mapCromwellState(cromwellRunStatus.getStatus());
        runStatus.setState(wesState);

        return runStatus;
    }

    /* ##################################################
       # GET RUN LOG
       ################################################## */
    
    public void completeRunLog(RunLog runLog) throws Exception {
        try {

            // make metadata request
            CloseableHttpClient httpClient = HttpClients.createDefault();
            String cromwellMetadataUrl = getLanguageConfig().getCromwellBaseUrl()
                + "api/workflows/v1/"
                + getWesRun().getCromwellRunId()
                + "/metadata";
            
            HttpGet getRequest = new HttpGet(cromwellMetadataUrl);
            CloseableHttpResponse response = httpClient.execute(getRequest);
            byte[] responseBodyBytes = response.getEntity().getContent().readAllBytes();
            String responseBodyString = new String(responseBodyBytes, StandardCharsets.UTF_8);
            ObjectMapper mapper = new ObjectMapper();
            CromwellWorkflowMetadata cromwellWorkflowMetadata = mapper.readValue(responseBodyString, CromwellWorkflowMetadata.class);

            // parse task logs
            List<WesLog> wesTaskLogs = new ArrayList<>();
            for (String callKey: cromwellWorkflowMetadata.getCalls().keySet()) {
                int i = 0;
                for (CromwellTaskMetadata cromwellTask : cromwellWorkflowMetadata.getCalls().get(callKey)) {
                    WesLog wesTaskLog = new WesLog();
                    String name = callKey + ".step-" + i;
                    wesTaskLog.setName(name);
                    // wesTaskLog.setCmd(cmd);
                    
                    wesTaskLog.setStartTime(LocalDateTime.parse(cromwellTask.getStart(), DateTimeFormatter.ofPattern(CROMWELL_DATE_FORMAT)));
                    wesTaskLog.setEndTime(LocalDateTime.parse(cromwellTask.getEnd(), DateTimeFormatter.ofPattern(CROMWELL_DATE_FORMAT)));
                    // wesTaskLog.setStdout(stdout);
                    // wesTaskLog.setStderr(stderr);
                    wesTaskLog.setExitCode(cromwellTask.getReturnCode());
                    wesTaskLogs.add(wesTaskLog);
                }
                i++;
            };
            runLog.setTaskLogs(wesTaskLogs);

            // parse workflow logs

            // make outputs request
            // parse outputs
            
            
        } catch (Exception ex) {
            
        }
    }

    public static State mapCromwellState(CromwellStatus cromwellStatus) {
        if (CROMWELL_STATE_MAP.containsKey(cromwellStatus)) {
            return CROMWELL_STATE_MAP.get(cromwellStatus);
        }
        return State.UNKNOWN;
    }
}
