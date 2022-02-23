package org.ga4gh.starterkit.wes.utils.requesthandler;

import org.ga4gh.starterkit.common.exception.BadRequestException;
import org.ga4gh.starterkit.common.exception.ResourceNotFoundException;
import org.ga4gh.starterkit.common.requesthandler.RequestHandler;
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
        System.out.print("--- HANDLE REQUEST 0 --- \n");
        // load the persisten WesRun by its id to obtain workflow language,
        // engine associated with the run
        RunLog runLog = new RunLog();
        runLog.setRunId(runId);
        runLog.setState(State.UNKNOWN);
        WesRun wesRun = hibernateUtil.readEntityObject(WesRun.class, runId, true);
        if (wesRun == null) {
            throw new ResourceNotFoundException("No WES Run by the id: " + runId);
        }
        runLog.setRequest(wesRun.toWesRequest());

        // allow the low-level RunManager to perform language/engine-dependent
        // methods to obtain run status
        try {
            RunManager runManager = runManagerFactory.createRunManager(wesRun);
            // 
            System.out.print("*** RUN TYPE DETAILS HANDLER: GET LANG CONFIG: *** \n");
            System.out.print("Version: " + runManager.getLanguageHandler().getLanguageConfig().getVersions() + "\n");
            System.out.print("Engine: " + runManager.getLanguageHandler().getLanguageConfig().getEngine() + "\n");
            System.out.print("Type; " + runManager.getLanguageHandler().getLanguageConfig().getType() + "\n");
            System.out.print("****************************** \n");
            System.out.print("*** RUN TYPE DETAILS HANDLER: GET ENG CONFIG: *** \n");
            System.out.print("Version: " + runManager.getLanguageHandler().getEngineHandler().getEngineConfig().getVersion() + "\n");
            System.out.print("Type: " + runManager.getLanguageHandler().getEngineHandler().getEngineConfig().getType() + "\n");
            System.out.print("****************************** \n");
            // System.out.print(runManager.getEngineConfig() + "\n");
            // System.out.print(runManager.getEngineHandler() + "\n");
            System.out.print("****************************** \n");
            LanguageHandler runTypeDetailsHandler = runManager.getLanguageHandler();
            // System.out.print("- RUN TYPE DETAILS HANDLER: - \n");
            // System.out.print(runTypeDetailsHandler + "\n");
            System.out.print("- RUN TYPE DETAILS HANDLER- DETERMINE STATUS: - \n");
            System.out.print(runTypeDetailsHandler.determineRunStatus() + "\n"); // nothing is printed here
            // System.out.print(runTypeDetailsHandler.determineRunStatus().getState() + "\n"); // this doesn't print anything
            runLog.setState(runTypeDetailsHandler.determineRunStatus().getState()); // GHA stuck here
            System.out.print("-- RUN LOG FROM HANDLER: -- \n");
            System.out.print(runLog.getRunId() + "\n");
            runTypeDetailsHandler.completeRunLog(runLog);
        } catch (Exception ex) {
            throw new BadRequestException("Could not load WES run log");
        }
        return runLog;
    }
}
