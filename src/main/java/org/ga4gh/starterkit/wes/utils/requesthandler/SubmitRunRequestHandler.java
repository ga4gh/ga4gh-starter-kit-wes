package org.ga4gh.starterkit.wes.utils.requesthandler;

import java.io.IOException;
import java.util.List;
import java.util.Map;
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
import org.ga4gh.starterkit.wes.utils.DrsUrlResolver;
import org.ga4gh.starterkit.wes.utils.hibernate.WesHibernateUtil;
import org.ga4gh.starterkit.wes.utils.runmanager.RunManager;
import org.ga4gh.starterkit.wes.utils.runmanager.RunManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Request handling logic for submitting a new run request
 * 
 * @see org.ga4gh.starterkit.wes.controller.Runs#createRun createRun
 */
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

    /**
     * Instantiates a new SubmitRunRequestHandler object
     */
    public SubmitRunRequestHandler() {

    }

    /**
     * Prepares the request handler with input params from the controller function
     * @param workflowType workflow language specification
     * @param workflowTypeVersion workflow language specification version
     * @param workflowUrl URL to workflow source
     * @param workflowParams raw JSON string of workflow run input parameters
     * @param tags raw JSON string indicating key:value tags
     * @param workflowAttachment string array indicating files to upload
     * @return the prepared request handler
     */
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

    /**
     * submits a new workflow run request and returns the id
     */
    public RunId handleRequest() {
        try {
            validateRunRequest();
            WesRun wesRun = prepareRun();
            String resolvedWorkflowParams = resolveWorkflowParams();
            if (resolvedWorkflowParams != null) {
                wesRun.setWorkflowParams(resolvedWorkflowParams);
            }
            launchRun(wesRun);
            return wesRun.toRunId();
        } catch (Exception ex) {
            throw new ConflictException("Could not register new WorkflowRun");
        }
    }

    /**
     * validates that run request parameters are valid
     * @throws BadRequestException an unsupported or malformed parameter was provided
     */
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

    /**
     * Performs initial preparation when request is valid
     * @return a persistent WesRun entity to track the workflow run job
     * @throws EntityExistsException a new WesRun instance could not be created
     */
    private WesRun prepareRun() throws EntityExistsException {
        // create the WesRun, and check if a WesRun by the same id already exists
        // in the database. if so, regenerate the WesRun
        WesRun wesRun = createWesRun();
        boolean exists = hibernateUtil.readEntityObject(WesRun.class, wesRun.getId(), false) != null;
        while (exists) {
            wesRun = createWesRun();
            exists = hibernateUtil.readEntityObject(WesRun.class, wesRun.getId(), false) != null;
        }
        // persist the WesRun and return the persistent instance
        hibernateUtil.createEntityObject(WesRun.class, wesRun);
        return wesRun;
    }

    /**
     * Launches the prepared workflow run
     * @param wesRun persistent WesRun entity tracking the workflow run job
     * @throws ConflictException an exception was encountered
     * @throws Exception an exception was encountered
     */
    private void launchRun(WesRun wesRun) throws ConflictException, Exception {
        // create a low-level RunManager instance from the factory, allow
        // the RunManager that has knowledge of the requested workflow type and
        // engine to handle the submission
        RunManager runLauncher = runLauncherFactory.createRunManager(wesRun);
        if (runLauncher == null) {
            throw new ConflictException("Could not setup or launch workflow run");
        }
        runLauncher.setupAndLaunchRun();
    }

    /**
     * Creates a new, non-persistent WesRun object based on input parameters
     * @return non-persistent WesRun object
     */
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

    /**
     * Resolve any DRS URLs in the input parameters to URLs or file paths, leaving any
     * non-DRS URLs untouched 
     * @return raw JSON string of modified input parameters
     */
    private String resolveWorkflowParams() {
        
        try {
            // read the input params JSON into a map
            ObjectMapper mapper = new ObjectMapper();
            Map workflowParamsMap = mapper.readValue(workflowParams, Map.class);
            for (Object key : workflowParamsMap.keySet()) {
                // if the value is found to have been a DRS URL, then resolve it
                // and override the DRS URL with the raw path
                String resolvedPathOrUrl = DrsUrlResolver.resolveAccessPathOrUrl(workflowParamsMap.get(key));
                if (resolvedPathOrUrl != null) {
                    workflowParamsMap.put(key, resolvedPathOrUrl);
                }
            }
            return mapper.writeValueAsString(workflowParamsMap);
        } catch (IOException ex) {
            throw new BadRequestException("Supplied workflow_params not valid JSON");
        }
    }
}
