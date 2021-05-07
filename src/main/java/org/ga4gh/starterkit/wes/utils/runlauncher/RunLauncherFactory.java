package org.ga4gh.starterkit.wes.utils.runlauncher;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import org.ga4gh.starterkit.wes.model.WorkflowEngine;
import org.ga4gh.starterkit.wes.model.WorkflowType;
import org.ga4gh.starterkit.wes.utils.runlauncher.setup.engine.NativeEngineRunSetup;
import org.ga4gh.starterkit.wes.utils.runlauncher.setup.engine.WorkflowEngineRunSetup;
import org.ga4gh.starterkit.wes.utils.runlauncher.setup.type.NextflowTypeRunSetup;
import org.ga4gh.starterkit.wes.utils.runlauncher.setup.type.WorkflowTypeRunSetup;

public class RunLauncherFactory {

    private static HashMap<WorkflowType, Class<? extends WorkflowTypeRunSetup>> typeSetupClasses = new HashMap<>(){{
        put(WorkflowType.NEXTFLOW, NextflowTypeRunSetup.class);
    }};

    private static HashMap<WorkflowEngine, Class<? extends WorkflowEngineRunSetup>> engineSetupClasses = new HashMap<>(){{
        put(WorkflowEngine.NATIVE, NativeEngineRunSetup.class);
    }};

    public RunLauncher createRunLauncher(String id, String workflowParams, String workflowUrl, WorkflowType workflowType, WorkflowEngine workflowEngine) {
        try {
            RunLauncher runLauncher = new RunLauncher(id, workflowParams, workflowUrl);
            WorkflowTypeRunSetup typeRunSetup = typeSetupClasses.get(workflowType).getDeclaredConstructor().newInstance();
            WorkflowEngineRunSetup engineRunSetup = engineSetupClasses.get(workflowEngine).getDeclaredConstructor().newInstance();
            runLauncher.setWorkflowTypeRunSetup(typeRunSetup);
            runLauncher.setWorkflowEngineRunSetup(engineRunSetup);
            return runLauncher;
        } catch (NoSuchMethodException | InvocationTargetException |
            InstantiationException | IllegalAccessException ex) {
            return null;
        }
    }
}
