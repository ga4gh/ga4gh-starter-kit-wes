package org.ga4gh.starterkit.wes.utils.runlauncher.setup.engine;

import org.ga4gh.starterkit.wes.model.WesRun;

public interface WorkflowEngineRunSetup {

    public void setWesRun(WesRun wesRun);
    public WesRun getWesRun();
    public void stageWorkingArea() throws Exception;
    public void setWorkflowCommand(String[] command);
    public void wrapWorkflowCommand();
    public void launchWrappedWorkflowCommand() throws Exception;
}
