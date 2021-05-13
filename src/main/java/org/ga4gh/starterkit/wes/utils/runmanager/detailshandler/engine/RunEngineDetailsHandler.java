package org.ga4gh.starterkit.wes.utils.runmanager.detailshandler.engine;

import java.util.List;
import java.util.Map;

import org.ga4gh.starterkit.wes.model.WesRun;
import org.ga4gh.starterkit.wes.utils.runmanager.detailshandler.type.RunTypeDetailsHandler;

public interface RunEngineDetailsHandler {

    // common operations
    public void setWesRun(WesRun wesRun);
    public WesRun getWesRun();
    public void setRunTypeDetailsHandler(RunTypeDetailsHandler runTypeDetailsHandler);
    public RunTypeDetailsHandler getRunTypeDetailsHandler();
    public List<String> provideDirectoryContents(String directory) throws Exception;

    // for launching workflow runs
    public void stageWorkingArea() throws Exception;
    public void launchWorkflowRunCommand(String[] workflowRunCommand) throws Exception;

    // for reading workflow run status
    public Map<String, String> getFileContentsToDetermineRunStatus(Map<String, String> requestedFileMap) throws Exception;

    // for reading workflow run info
    // public Map<String, String> getFileContentsToDetermineRunInfo(Map<String, String> requestedFileMap);

}
