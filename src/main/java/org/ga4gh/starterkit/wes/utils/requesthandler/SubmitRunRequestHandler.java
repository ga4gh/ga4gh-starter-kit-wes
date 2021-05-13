package org.ga4gh.starterkit.wes.utils.requesthandler;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ga4gh.starterkit.common.exception.BadRequestException;
import org.ga4gh.starterkit.common.exception.ConflictException;
import org.ga4gh.starterkit.common.hibernate.exception.EntityExistsException;
import org.ga4gh.starterkit.common.requesthandler.RequestHandler;
import org.ga4gh.starterkit.wes.model.WesServiceInfo;
import org.ga4gh.starterkit.wes.model.WorkflowEngine;
import org.ga4gh.starterkit.wes.model.RunId;
import org.ga4gh.starterkit.wes.model.WesRun;
import org.ga4gh.starterkit.wes.model.WorkflowType;
import org.ga4gh.starterkit.wes.utils.hibernate.WesHibernateUtil;
import org.ga4gh.starterkit.wes.utils.runmanager.RunManager;
import org.ga4gh.starterkit.wes.utils.runmanager.RunManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class SubmitRunRequestHandler implements RequestHandler<RunId> {

    @Autowired
    private WesServiceInfo serviceInfo;

    @Autowired
    private WesHibernateUtil hibernateUtil;

    @Autowired
    private RunManagerFactory runLauncherFactory;

    private WorkflowType workflowType;
    private String workflowTypeVersion;
    private String workflowUrl;
    private String workflowParams;
    private String tags;
    private List<String> workflowAttachment;

    public SubmitRunRequestHandler() {

    }

    public SubmitRunRequestHandler prepare(WorkflowType workflowType,
        String workflowTypeVersion, String workflowUrl,
        String workflowParams, String tags, List<String> workflowAttachment
    ) {
        this.workflowType = workflowType;
        this.workflowTypeVersion = workflowTypeVersion;
        this.workflowUrl = workflowUrl;
        this.workflowParams = workflowParams;
        this.tags = tags;
        this.workflowAttachment = workflowAttachment;
        return this;
    }

    public RunId handleRequest() {
        try {
            validateRunRequest();
            WesRun wesRun = prepareRun();
            launchRun(wesRun);
            return wesRun.toRunId();
        } catch (Exception ex) {
            throw new ConflictException("Could not register new WorkflowRun");
        }
    }

    private void validateRunRequest() throws BadRequestException {
        // Validate workflowType
        // - assert requested workflowType is supported according to ServiceInfo
        if (!serviceInfo.isWorkflowTypeSupported(workflowType)) {
            throw new BadRequestException(
                "Unsupported workflow_type: '" + workflowType
                + "'. Supported workflow types: " + serviceInfo.getWorkflowTypeVersions().keySet()
            );
        }
        
        // Validate workflowTypeVersion
        // - assert requested version is supported according to ServiceInfo
        if (!serviceInfo.isWorkflowTypeVersionSupported(workflowType, workflowTypeVersion)) {
            throw new BadRequestException(
                "Unsupported workflow_type_version: '"
                + workflowTypeVersion
                + "'. Supported workflow type versions: "
                + serviceInfo.getWorkflowTypeVersions().get(workflowType)
            );
        }

        // Validate workflowParams
        // - assert it is valid JSON
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.readTree(workflowParams);
        } catch (IOException e) {
            throw new BadRequestException("Supplied workflow_params not valid JSON");
        }

        // TODO add workflow_url validation

        // 'tags' not evaluated, not supported
    }

    private WesRun prepareRun() throws EntityExistsException {
        WesRun wesRun = createWesRun();
        boolean exists = hibernateUtil.readEntityObject(WesRun.class, wesRun.getId(), false) != null;
        while (exists) {
            wesRun = createWesRun();
            exists = hibernateUtil.readEntityObject(WesRun.class, wesRun.getId(), false) != null;
        }
        hibernateUtil.createEntityObject(WesRun.class, wesRun);
        return wesRun;
    }

    private void launchRun(WesRun wesRun) throws ConflictException, Exception {
        RunManager runLauncher = runLauncherFactory.createRunLauncher(wesRun);
        if (runLauncher == null) {
            throw new ConflictException("Could not setup or launch workflow run");
        }
        runLauncher.setupAndLaunchRun();
    }

    private WesRun createWesRun() {
        WesRun wesRun = new WesRun();
        wesRun.setId(UUID.randomUUID().toString());
        wesRun.setWorkflowType(workflowType);
        wesRun.setWorkflowTypeVersion(workflowTypeVersion);
        wesRun.setWorkflowUrl(workflowUrl);
        wesRun.setWorkflowParams(workflowParams);
        
        // TODO this is hardcoded to NATIVE workflow engine, parameterize to
        // allow other workflow engines
        wesRun.setWorkflowEngine(WorkflowEngine.NATIVE);
        wesRun.setWorkflowEngineVersion(null);

        return wesRun;
    }
}
