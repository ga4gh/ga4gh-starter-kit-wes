package org.ga4gh.starterkit.wes.utils.requesthandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ga4gh.starterkit.common.exception.BadRequestException;
import org.ga4gh.starterkit.common.exception.ResourceNotFoundException;
import org.ga4gh.starterkit.common.requesthandler.RequestHandler;
import org.ga4gh.starterkit.common.util.logging.LoggingUtil;
import org.ga4gh.starterkit.wes.model.RunLog;
import org.ga4gh.starterkit.wes.model.State;
import org.ga4gh.starterkit.wes.model.WesRun;
import org.ga4gh.starterkit.wes.utils.hibernate.WesHibernateUtil;
import org.ga4gh.starterkit.wes.utils.runmanager.RunManager;
import org.ga4gh.starterkit.wes.utils.runmanager.RunManagerFactory;
import org.ga4gh.starterkit.wes.utils.runmanager.language.LanguageHandler;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Request handling logic for getting full log information (according to the WES spec)
 * from a requested workflow run
 * 
 * @see org.ga4gh.starterkit.wes.controller.Runs#getRunLog getRunLog
 */
public class GetRunLogRequestHandler implements RequestHandler<RunLog> {

    @Autowired
    private RunManagerFactory runManagerFactory;

    @Autowired
    private WesHibernateUtil hibernateUtil;

    private String runId;

    @Autowired
    private LoggingUtil loggingUtil;

    /**
     * Instantiates a new GetRunLogRequestHandler
     */
    public GetRunLogRequestHandler() {

    }

    /**
     * Prepares the request handler with input params from the controller function
     * @param runId run identifier
     * @return the prepared request handler
     */
    public GetRunLogRequestHandler prepare(String runId) {
        this.runId = runId;
        return this;
    }

    /**
     * Obtains full log information for the requested workflow run
     */
    public RunLog handleRequest() {
        // load the persisten WesRun by its id to obtain workflow language,
        // engine associated with the run
        WesRun wesRun = hibernateUtil.readEntityObject(WesRun.class, runId, true);
        if (wesRun == null) {
            throw new ResourceNotFoundException("No WES Run by the id: " + runId);
        }

        // if the run log JSON has been previously generated, load from db
        // and return
        if (wesRun.getFinalRunLogJson() != null) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                return mapper.readValue(wesRun.getFinalRunLogJson(), RunLog.class);
            } catch (JsonProcessingException ex) {
                System.out.println("Could not load RunLog from pre-existing JSON, attempting to load directly");
            }
        }

        // load the RunLog directly by custom API methods for Nextflow, WDL, etc.
        RunLog runLog = new RunLog();
        runLog.setRunId(runId);
        runLog.setState(State.UNKNOWN);
        runLog.setRequest(wesRun.toWesRequest());

        // allow the low-level RunManager to perform language/engine-dependent
        // methods to obtain run status
        try {
            RunManager runManager = runManagerFactory.createRunManager(wesRun);
            LanguageHandler runTypeDetailsHandler = runManager.getLanguageHandler();
            runLog.setState(runTypeDetailsHandler.determineRunStatus().getState()); 
            runTypeDetailsHandler.completeRunLog(runLog);

            // if the run is in a final state (e.g. complete, then write the
            // JSON to the DB to be loaded later
            if (runLog.getState() == State.COMPLETE) {
                ObjectMapper mapper = new ObjectMapper();
                String finalRunLogJson = mapper.writeValueAsString(runLog);
                wesRun.setFinalRunLogJson(finalRunLogJson);
                hibernateUtil.updateEntityObject(WesRun.class, wesRun.getId(), wesRun);
            }
        } catch (Exception ex) {
            loggingUtil.error("Exception occurred: Could not load WES run log. Original Exception message: " + ex.getMessage());
            throw new BadRequestException("Could not load WES run log");
        }
        return runLog;
    }
}
