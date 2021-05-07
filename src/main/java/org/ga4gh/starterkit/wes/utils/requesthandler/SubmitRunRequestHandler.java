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
import org.ga4gh.starterkit.wes.utils.runlauncher.RunLauncher;
import org.ga4gh.starterkit.wes.utils.runlauncher.RunLauncherFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class SubmitRunRequestHandler implements RequestHandler<RunId> {

    @Autowired
    private WesServiceInfo serviceInfo;

    @Autowired
    private WesHibernateUtil hibernateUtil;

    @Autowired
    private RunLauncherFactory runLauncherFactory;

    private String workflowParams;
    private WorkflowType workflowType;
    private String workflowTypeVersion;
    private String tags;
    private String workflowUrl;
    private List<String> workflowAttachment;

    public SubmitRunRequestHandler() {

    }

    public SubmitRunRequestHandler prepare(String workflowParams,
        WorkflowType workflowType, String workflowTypeVersion,
        String tags, String workflowUrl, List<String> workflowAttachment
    ) {
        this.workflowParams = workflowParams;
        this.workflowType = workflowType;
        this.workflowTypeVersion = workflowTypeVersion;
        this.tags = tags;
        this.workflowUrl = workflowUrl;
        this.workflowAttachment = workflowAttachment;
        return this;
    }

    public RunId handleRequest() {
        try {
            validateRunRequest();
            WesRun wesRun = prepareRun();
            launchRun(wesRun);
            return wesRun.toRunId();
        } catch (EntityExistsException ex) {
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

    private void launchRun(WesRun wesRun) throws ConflictException {
        RunLauncher runLauncher = runLauncherFactory.createRunLauncher(wesRun.getId(), workflowParams, workflowUrl, workflowType, WorkflowEngine.NATIVE);
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

        // TODO this is hardcoded to NATIVE workflow engine, parameterize to
        // allow other workflow engines
        wesRun.setWorkflowEngine(WorkflowEngine.NATIVE);
        wesRun.setWorkflowEngineVersion(null);

        return wesRun;
    }
}
