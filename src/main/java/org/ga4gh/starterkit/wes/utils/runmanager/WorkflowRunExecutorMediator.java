package org.ga4gh.starterkit.wes.utils.runmanager;

import java.util.HashMap;
import java.util.concurrent.Callable;

import org.ga4gh.starterkit.wes.model.WorkflowEngine;
import org.ga4gh.starterkit.wes.model.WorkflowType;

public class WorkflowRunExecutorMediator {

    private RunManager runManager;

    private final HashMap<WorkflowType, HashMap<WorkflowEngine, Callable<Void>>> methodMap = new HashMap<>() {{
        put(
            WorkflowType.NEXTFLOW,
            new HashMap<WorkflowEngine, Callable<Void>>() {{
                put(WorkflowEngine.NATIVE, () -> doNothing());
                put(WorkflowEngine.SLURM, () -> nextflowSlurm());
            }}
        );
    }};

    public void mediateWorkflowRun() throws Exception {
        WorkflowType workflowType = getRunManager().getWesRun().getWorkflowType();
        WorkflowEngine workflowEngine = getRunManager().getEngineHandler().getEngineConfig().getType();
        Callable<Void> method = methodMap.get(workflowType).get(workflowEngine);
        method.call();
    }

    private Void doNothing() {
        return null;
    }

    private Void nextflowSlurm() {
        String filePath = "nextflow.config";
        String content = "process.executor = 'slurm'\n";
        getRunManager().getEngineHandler().writeContentToFile(filePath, content);
        return null;
    }

    /* Setters and Getters */

    public void setRunManager(RunManager runManager) {
        this.runManager = runManager;
    }

    public RunManager getRunManager() {
        return runManager;
    }
}
