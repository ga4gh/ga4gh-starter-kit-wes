package org.ga4gh.starterkit.wes.utils.runmanager.detailshandler.type;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.ga4gh.starterkit.wes.model.RunLog;
import org.ga4gh.starterkit.wes.model.RunStatus;
import org.ga4gh.starterkit.wes.model.WesRun;
import org.ga4gh.starterkit.wes.utils.runmanager.detailshandler.engine.RunEngineDetailsHandler;

public interface RunTypeDetailsHandler {

    // common operations
    public void setWesRun(WesRun wesRun);
    public WesRun getWesRun();
    public void setRunEngineDetailsHandler(RunEngineDetailsHandler runEngineDetailsHandler);
    public RunEngineDetailsHandler getRunEngineDetailsHandler();

    // for launching workflow runs
    public String[] constructWorkflowRunCommand() throws Exception;

    // for reading workflow run status
    public Map<String, String> requestFileContentsToDetermineRunStatus() throws Exception;
    public RunStatus determineRunStatus(Map<String, String> requestedFileContentsMap);

    // for reading workflow run info
    // public Map<String, String> requestFileContentsToDetermineRunInfo();
    // public RunLog determineRunLog(Map<String, String> requestedFileContents);
}
