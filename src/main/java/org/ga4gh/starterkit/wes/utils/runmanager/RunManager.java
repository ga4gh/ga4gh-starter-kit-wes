package org.ga4gh.starterkit.wes.utils.runmanager;

import org.ga4gh.starterkit.wes.config.engine.EngineConfig;
import org.ga4gh.starterkit.wes.config.language.LanguageConfig;
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
    private LanguageConfig languageConfig;
    private EngineConfig engineConfig;
    private LanguageHandler languageHandler;
    private EngineHandler engineHandler;

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
        getEngineHandler().stageWorkingArea();
        String[] workflowRunCommand = getLanguageHandler().constructWorkflowRunCommand();
        WorkflowRunExecutorMediator mediator = new WorkflowRunExecutorMediator();
        mediator.setRunManager(this);
        mediator.mediateWorkflowRun();
        getEngineHandler().launchWorkflowRunCommand(workflowRunCommand);
    }

    /**
     * Gets workflow run status according to the specifics of the workflow language
     * and engine used.
     * @return status of the run of interest
     * @throws Exception server-side operation could not be completed
     */
    public RunStatus getRunStatus() throws Exception {
        return getLanguageHandler().determineRunStatus();
    }

    public void setWesRun(WesRun wesRun) {
        this.wesRun = wesRun;
    }

    public WesRun getWesRun() {
        return wesRun;
    }

    public void setLanguageConfig(LanguageConfig languageConfig) {
        this.languageConfig = languageConfig;
    }

    public LanguageConfig getLanguageConfig() {
        return languageConfig;
    }

    public void setEngineConfig(EngineConfig engineConfig) {
        this.engineConfig = engineConfig;
    }

    public EngineConfig getEngineConfig() {
        return engineConfig;
    }

    public void setLanguageHandler(LanguageHandler languageHandler) {
        this.languageHandler = languageHandler;
    }

    public LanguageHandler getLanguageHandler() {
        return languageHandler;
    }

    public void setEngineHandler(EngineHandler engineHandler) {
        this.engineHandler = engineHandler;
    }

    public EngineHandler getEngineHandler() {
        return engineHandler;
    }
}
