package org.ga4gh.starterkit.wes.utils.runmanager;

import java.util.Map;

import org.ga4gh.starterkit.wes.model.RunStatus;
import org.ga4gh.starterkit.wes.model.State;
import org.ga4gh.starterkit.wes.model.WesRun;
import org.ga4gh.starterkit.wes.utils.runmanager.detailshandler.engine.RunEngineDetailsHandler;
import org.ga4gh.starterkit.wes.utils.runmanager.detailshandler.type.RunTypeDetailsHandler;

public class RunManager {

    private WesRun wesRun;
    private RunTypeDetailsHandler runTypeDetailsHandler;
    private RunEngineDetailsHandler runEngineDetailsHandler;

    public RunManager() {

    }

    public RunManager(WesRun wesRun) {
        this.wesRun = wesRun;
    }

    public void setupAndLaunchRun() throws Exception {
        runEngineDetailsHandler.stageWorkingArea();
        String[] workflowRunCommand = runTypeDetailsHandler.constructWorkflowRunCommand();
        runEngineDetailsHandler.launchWorkflowRunCommand(workflowRunCommand);
    }

    public RunStatus getRunStatus() throws Exception {
        Map<String, String> requestedFilesForStatus = runTypeDetailsHandler.requestFileContentsToDetermineRunStatus();
        Map<String, String> requestedFileContentsForStatus = runEngineDetailsHandler.getFileContentsToDetermineRunStatus(requestedFilesForStatus);
        return runTypeDetailsHandler.determineRunStatus(requestedFileContentsForStatus);
    }

    public void setWesRun(WesRun wesRun) {
        this.wesRun = wesRun;
    }

    public WesRun getWesRun() {
        return wesRun;
    }

    public void setRunTypeDetailsHandler(RunTypeDetailsHandler runTypeDetailsHandler) {
        this.runTypeDetailsHandler = runTypeDetailsHandler;
    }

    public RunTypeDetailsHandler getRunTypeDetailsHandler() {
        return runTypeDetailsHandler;
    }

    public void setRunEngineDetailsHandler(RunEngineDetailsHandler runEngineDetailsHandler) {
        this.runEngineDetailsHandler = runEngineDetailsHandler;
    }

    public RunEngineDetailsHandler getRunEngineDetailsHandler() {
        return runEngineDetailsHandler;
    }
}
