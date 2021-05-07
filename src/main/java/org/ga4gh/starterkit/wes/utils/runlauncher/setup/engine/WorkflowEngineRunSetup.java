package org.ga4gh.starterkit.wes.utils.runlauncher.setup.engine;

public interface WorkflowEngineRunSetup {

    public void stageWorkingArea();
    public void setWorkflowCommand(String[] command);
    public void wrapWorkflowCommand();
    public void launchWrappedWorkflowCommand();
    
}
