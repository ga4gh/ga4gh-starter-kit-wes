package org.ga4gh.starterkit.wes.utils.runlauncher;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import org.ga4gh.starterkit.wes.model.WesRun;
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

    public RunLauncher createRunLauncher(WesRun wesRun) {
        try {
            RunLauncher runLauncher = new RunLauncher(wesRun);
            WorkflowTypeRunSetup typeRunSetup = typeSetupClasses.get(wesRun.getWorkflowType()).getDeclaredConstructor().newInstance();
            WorkflowEngineRunSetup engineRunSetup = engineSetupClasses.get(wesRun.getWorkflowEngine()).getDeclaredConstructor().newInstance();
            typeRunSetup.setWesRun(wesRun);
            engineRunSetup.setWesRun(wesRun);
            runLauncher.setWorkflowTypeRunSetup(typeRunSetup);
            runLauncher.setWorkflowEngineRunSetup(engineRunSetup);
            return runLauncher;
        } catch (NoSuchMethodException | InvocationTargetException |
            InstantiationException | IllegalAccessException ex) {
            return null;
        }
    }
}
