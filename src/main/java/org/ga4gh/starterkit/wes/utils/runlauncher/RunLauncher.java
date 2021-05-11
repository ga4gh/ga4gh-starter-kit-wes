package org.ga4gh.starterkit.wes.utils.runlauncher;

import org.ga4gh.starterkit.wes.model.WesRun;
import org.ga4gh.starterkit.wes.utils.runlauncher.setup.engine.WorkflowEngineRunSetup;
import org.ga4gh.starterkit.wes.utils.runlauncher.setup.type.WorkflowTypeRunSetup;

public class RunLauncher {

    private WesRun wesRun;
    private WorkflowTypeRunSetup workflowTypeRunSetup;
    private WorkflowEngineRunSetup workflowEngineRunSetup;

    public RunLauncher() {

    }

    public RunLauncher(WesRun wesRun) {
        this.wesRun = wesRun;
    }

    public void setupAndLaunchRun() throws Exception {
        workflowEngineRunSetup.stageWorkingArea();
        String[] command = workflowTypeRunSetup.constructCommand();
        workflowEngineRunSetup.setWorkflowCommand(command);
        workflowEngineRunSetup.wrapWorkflowCommand();
        workflowEngineRunSetup.launchWrappedWorkflowCommand();
    }

    public void setWesRun(WesRun wesRun) {
        this.wesRun = wesRun;
    }

    public WesRun getWesRun() {
        return wesRun;
    }

    public void setWorkflowTypeRunSetup(WorkflowTypeRunSetup workflowTypeRunSetup) {
        this.workflowTypeRunSetup = workflowTypeRunSetup;
    }

    public WorkflowTypeRunSetup getWorkflowTypeRunSetup() {
        return workflowTypeRunSetup;
    }

    public void setWorkflowEngineRunSetup(WorkflowEngineRunSetup workflowEngineRunSetup) {
        this.workflowEngineRunSetup = workflowEngineRunSetup;
    }

    public WorkflowEngineRunSetup getWorkflowEngineRunSetup() {
        return workflowEngineRunSetup;
    }
}
