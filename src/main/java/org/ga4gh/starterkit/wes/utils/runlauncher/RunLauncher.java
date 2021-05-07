package org.ga4gh.starterkit.wes.utils.runlauncher;

import org.ga4gh.starterkit.wes.utils.runlauncher.setup.engine.WorkflowEngineRunSetup;
import org.ga4gh.starterkit.wes.utils.runlauncher.setup.type.WorkflowTypeRunSetup;

public class RunLauncher {

    private String id;
    private String workflowParams;
    private String workflowUrl;
    private WorkflowTypeRunSetup workflowTypeRunSetup;
    private WorkflowEngineRunSetup workflowEngineRunSetup;

    public RunLauncher() {

    }

    public RunLauncher(String id, String workflowParams, String workflowUrl) {
        this.id = id;
        this.workflowParams = workflowParams;
        this.workflowUrl = workflowUrl;
    }

    public void setupAndLaunchRun() {
        workflowEngineRunSetup.stageWorkingArea();
        String[] command = workflowTypeRunSetup.constructCommand();
        workflowEngineRunSetup.setWorkflowCommand(command);
        workflowEngineRunSetup.wrapWorkflowCommand();
        workflowEngineRunSetup.launchWrappedWorkflowCommand();
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setWorkflowParams(String workflowParams) {
        this.workflowParams = workflowParams;
    }

    public String getWorkflowParams() {
        return workflowParams;
    }

    public void setWorkflowUrl(String workflowUrl) {
        this.workflowUrl = workflowUrl;
    }

    public String getWorkflowUrl() {
        return workflowUrl;
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
