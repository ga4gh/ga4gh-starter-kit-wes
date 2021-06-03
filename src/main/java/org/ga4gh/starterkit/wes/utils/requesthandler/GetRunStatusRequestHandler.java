package org.ga4gh.starterkit.wes.utils.requesthandler;

import org.ga4gh.starterkit.common.exception.BadRequestException;
import org.ga4gh.starterkit.common.exception.ResourceNotFoundException;
import org.ga4gh.starterkit.common.requesthandler.RequestHandler;
import org.ga4gh.starterkit.wes.model.RunStatus;
import org.ga4gh.starterkit.wes.model.State;
import org.ga4gh.starterkit.wes.model.WesRun;
import org.ga4gh.starterkit.wes.utils.hibernate.WesHibernateUtil;
import org.ga4gh.starterkit.wes.utils.runmanager.RunManager;
import org.ga4gh.starterkit.wes.utils.runmanager.RunManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Request handling logic for getting run status
 * 
 * @see org.ga4gh.starterkit.wes.controller.Runs#runStatus runStatus
 */
public class GetRunStatusRequestHandler implements RequestHandler<RunStatus> {

    @Autowired
    private RunManagerFactory runManagerFactory;

    @Autowired
    private WesHibernateUtil hibernateUtil;

    private String runId;

    /**
     * Instantiates a new GetRunStatusRequestHandler object
     */
    public GetRunStatusRequestHandler() {

    }

    /**
     * Prepares the request handler with input params from the controller function
     * @param runId run identifier
     * @return the prepared request handler
     */
    public GetRunStatusRequestHandler prepare(String runId) {
        this.runId = runId;
        return this;
    }

    /**
     * obtains the status of the requested workflow run
     */
    public RunStatus handleRequest() {
        // load the persistent WesRun by it's id to obtain details (workflow
        //language, engine used)
        RunStatus runStatus = new RunStatus();
        runStatus.setRunId(runId);
        runStatus.setState(State.UNKNOWN);
        WesRun wesRun = hibernateUtil.readEntityObject(WesRun.class, runId, true);
        if (wesRun == null) {
            throw new ResourceNotFoundException("No WES Run by the id: " + runId);
        }

        // allow the low-level RunManager to perform language/engine-dependent
        // methods to obtain run status
        try {
            RunManager runManager = runManagerFactory.createRunManager(wesRun);
            return runManager.getRunStatus();
        } catch (Exception ex) {
            throw new BadRequestException("Could not load WES run status");
        }
    }
}
