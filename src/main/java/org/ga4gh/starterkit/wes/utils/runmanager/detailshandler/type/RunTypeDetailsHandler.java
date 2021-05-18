package org.ga4gh.starterkit.wes.utils.runmanager.detailshandler.type;

import java.util.List;
import java.util.Map;

import org.ga4gh.starterkit.wes.model.RunLog;
import org.ga4gh.starterkit.wes.model.RunStatus;
import org.ga4gh.starterkit.wes.model.WesLog;
import org.ga4gh.starterkit.wes.model.WesRun;
import org.ga4gh.starterkit.wes.utils.runmanager.detailshandler.engine.RunEngineDetailsHandler;

public interface RunTypeDetailsHandler {

    // common operations
    public void setWesRun(WesRun wesRun);
    public WesRun getWesRun();
    public void setRunEngineDetailsHandler(RunEngineDetailsHandler runEngineDetailsHandler);
    public RunEngineDetailsHandler getRunEngineDetailsHandler();
    public String requestFileContentsFromEngine(String filename) throws Exception;
    public String requestCommandStdoutFromEngine(String[] command) throws Exception;

    // for launching workflow runs
    public String[] constructWorkflowRunCommand() throws Exception;

    // for reading workflow run status
    public RunStatus determineRunStatus() throws Exception;

    // for reading workflow run info
    public void completeRunLog(RunLog runLog) throws Exception;
}
