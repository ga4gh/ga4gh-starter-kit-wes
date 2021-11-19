package org.ga4gh.starterkit.wes.utils.runmanager;

import org.ga4gh.starterkit.wes.model.RunStatus;
import org.ga4gh.starterkit.wes.model.WesRun;
import org.ga4gh.starterkit.wes.utils.runmanager.engine.EngineHandler;
import org.ga4gh.starterkit.wes.utils.runmanager.language.LanguageHandler;

/**
 * Container for performing low-level inspections/detail management of a 
 * workflow run. A RunManager has both a runTypeDetailsHandler and a 
 * runEngineDetailsHandler, which contain operations for accessing data according
 * to the workflow language used, and the workflow engine on which the run was 
 * launched respectively.
 */
public class RunManager {

    private WesRun wesRun;
    private LanguageHandler runTypeDetailsHandler;
    private EngineHandler runEngineDetailsHandler;

    /**
     * Instantiates a new RunManager instance
     */
    public RunManager() {

    }

    /**
     * Instantiates a new RunManager instance
     * @param wesRun WesRun entity indicating workflow run
     */
    public RunManager(WesRun wesRun) {
        this.wesRun = wesRun;
    }

    /**
     * Launches a workflow run according to the specifics of the workflow language
     * and engine used.
     * @throws Exception server-side operation could not be completed
     */
    public void setupAndLaunchRun() throws Exception {
        // a base working dir is setup according to the workflow engine,
        // the raw CLI that the workflow language uses is then constructed
        // and launched through the engine
        runEngineDetailsHandler.stageWorkingArea();
        String[] workflowRunCommand = runTypeDetailsHandler.constructWorkflowRunCommand();
        runEngineDetailsHandler.launchWorkflowRunCommand(workflowRunCommand);
    }

    /**
     * Gets workflow run status according to the specifics of the workflow language
     * and engine used.
     * @return status of the run of interest
     * @throws Exception server-side operation could not be completed
     */
    public RunStatus getRunStatus() throws Exception {
        return runTypeDetailsHandler.determineRunStatus();
    }

    /**
     * Assign wesRun
     * @param wesRun WesRun entity
     */
    public void setWesRun(WesRun wesRun) {
        this.wesRun = wesRun;
    }

    /**
     * Retrieve wesRun
     * @return WesRun entity
     */
    public WesRun getWesRun() {
        return wesRun;
    }

    /**
     * Assign runTypeDetailsHandler
     * @param runTypeDetailsHandler handles data access according to workflow language used
     */
    public void setRunTypeDetailsHandler(LanguageHandler runTypeDetailsHandler) {
        this.runTypeDetailsHandler = runTypeDetailsHandler;
    }

    /**
     * Retrieve runTypeDetailsHandler
     * @return handles data access according to workflow language used
     */
    public LanguageHandler getRunTypeDetailsHandler() {
        return runTypeDetailsHandler;
    }

    /**
     * Assign runEngineDetailsHandler
     * @param runEngineDetailsHandler handles data access according to workflow engine used
     */
    public void setRunEngineDetailsHandler(EngineHandler runEngineDetailsHandler) {
        this.runEngineDetailsHandler = runEngineDetailsHandler;
    }

    /**
     * Retrieve runEngineDetailsHandler
     * @return handles data access according to workflow engine used
     */
    public EngineHandler getRunEngineDetailsHandler() {
        return runEngineDetailsHandler;
    }
}
