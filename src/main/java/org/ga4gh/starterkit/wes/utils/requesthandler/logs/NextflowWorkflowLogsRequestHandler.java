package org.ga4gh.starterkit.wes.utils.requesthandler.logs;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.util.Strings;
import org.ga4gh.starterkit.common.exception.ResourceNotFoundException;
import org.ga4gh.starterkit.common.requesthandler.RequestHandler;
import org.ga4gh.starterkit.wes.model.WesRun;
import org.ga4gh.starterkit.wes.utils.hibernate.WesHibernateUtil;
import org.ga4gh.starterkit.wes.utils.runmanager.RunManager;
import org.ga4gh.starterkit.wes.utils.runmanager.RunManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class NextflowWorkflowLogsRequestHandler implements RequestHandler<String> {

    @Autowired
    private RunManagerFactory runManagerFactory;

    @Autowired
    private WesHibernateUtil hibernateUtil;

    private String channel;
    private String runId;
    private String workdirs;

    public NextflowWorkflowLogsRequestHandler prepare(String channel, String runId, String workdirs) {
        this.channel = channel;
        this.runId = runId;
        this.workdirs = workdirs;
        return this;
    }

    public String handleRequest() {
        List<String> workflowLogs = new ArrayList<>();

        WesRun wesRun = hibernateUtil.readEntityObject(WesRun.class, runId, true);
        if (wesRun == null) {
            throw new ResourceNotFoundException("No WES Run by the id: " + runId);
        }
        RunManager runManager = runManagerFactory.createRunLauncher(wesRun);
        String channelFileSuffix = channel.equals("stdout") ? "out" : "err";
        String[] workdirsSplit = workdirs.split(",");

        try {
            for (String workdir : workdirsSplit) {
                Path filePath = Paths.get("work", workdir, ".command." + channelFileSuffix);
                String logfileContents = runManager.getRunEngineDetailsHandler().getRequestedFileContents(filePath.toString());
                workflowLogs.add(logfileContents);
            }
        } catch (Exception ex) {
            return null;
        }
        
        return Strings.join(workflowLogs, '\n');
    }

    /* Setters and getters */

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getChannel() {
        return channel;
    }

    public void setRunId(String runId) {
        this.runId = runId;
    }

    public String getRunId() {
        return runId;
    }

    public void setWorkdirs(String workdirs) {
        this.workdirs = workdirs;
    }

    public String getWorkdirs() {
        return workdirs;
    }
}
