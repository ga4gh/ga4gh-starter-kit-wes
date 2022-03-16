package org.ga4gh.starterkit.wes.utils.runmanager;

import java.util.HashMap;
import java.util.concurrent.Callable;
import org.ga4gh.starterkit.wes.model.ContainerizationType;
import org.ga4gh.starterkit.wes.model.WorkflowEngine;
import org.ga4gh.starterkit.wes.model.WorkflowType;

public class WorkflowRunExecutorMediator {

    private RunManager runManager;

    private final HashMap<WorkflowType, HashMap<WorkflowEngine, Callable<Void>>> methodMap = new HashMap<>() {{
        put(
            WorkflowType.NEXTFLOW,
            new HashMap<WorkflowEngine, Callable<Void>>() {{
                put(WorkflowEngine.NATIVE, () -> nextflowNative());
                put(WorkflowEngine.SLURM, () -> nextflowSlurm());
            }}
        );
        put(
            WorkflowType.WDL,
            new HashMap<WorkflowEngine, Callable<Void>>() {{
                put(WorkflowEngine.NATIVE, () -> doNothing());
                put(WorkflowEngine.SLURM, () -> doNothing());
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

    /* Language-Engine specific steps */

    private Void nextflowNative() {
        String filePath = "nextflow.config";
        String content = nextflowConfigForContainerizationType();
        getRunManager().getEngineHandler().writeContentToFile(filePath, content);
        return null;
    }

    private Void nextflowSlurm() {
        String filePath = "nextflow.config";
        String content = "process.executor = 'slurm'\n"
            + nextflowConfigForContainerizationType();
        getRunManager().getEngineHandler().writeContentToFile(filePath, content);
        return null;
    }

    /* Helper Functions */

    private String nextflowConfigForContainerizationType() {
        ContainerizationType ct = getRunManager().getEngineHandler().getEngineConfig().getContainerizationType();
        StringBuffer sb = new StringBuffer();
        switch(ct) {
            case DOCKER:
                sb.append("docker.enabled = true\n");
                sb.append("singularity.enabled = false\n");
                break;
            case SINGULARITY:
                sb.append("docker.enabled = false\n");
                sb.append("singularity.enabled = true\n");
                sb.append("singularity.autoMounts = true\n");
                break;
            default:
                sb.append("docker.enabled = true\n");
                sb.append("singularity.enabled = false\n");
                break;
        }
        return sb.toString();
    }

    /* Setters and Getters */

    public void setRunManager(RunManager runManager) {
        this.runManager = runManager;
    }

    public RunManager getRunManager() {
        return runManager;
    }
}
