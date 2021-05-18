package org.ga4gh.starterkit.wes.utils.runmanager.detailshandler.engine;

import java.util.List;
import org.ga4gh.starterkit.wes.model.WesRun;
import org.ga4gh.starterkit.wes.utils.runmanager.detailshandler.type.RunTypeDetailsHandler;

public interface RunEngineDetailsHandler {

    // common operations
    public void setWesRun(WesRun wesRun);
    public WesRun getWesRun();
    public void setRunTypeDetailsHandler(RunTypeDetailsHandler runTypeDetailsHandler);
    public RunTypeDetailsHandler getRunTypeDetailsHandler();
    public List<String> provideDirectoryContents(String directory) throws Exception;
    public String getRequestedFileContents(String filename) throws Exception;
    public String getRequestedCommandStdout(String[] command) throws Exception;

    // for launching workflow runs
    public void stageWorkingArea() throws Exception;
    public void launchWorkflowRunCommand(String[] workflowRunCommand) throws Exception;
}
