package org.ga4gh.starterkit.wes.utils.runmanager;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import org.ga4gh.starterkit.wes.model.WesRun;
import org.ga4gh.starterkit.wes.model.WorkflowEngine;
import org.ga4gh.starterkit.wes.model.WorkflowType;
import org.ga4gh.starterkit.wes.utils.runmanager.detailshandler.engine.NativeEngineDetailsHandler;
import org.ga4gh.starterkit.wes.utils.runmanager.detailshandler.engine.RunEngineDetailsHandler;
import org.ga4gh.starterkit.wes.utils.runmanager.detailshandler.type.NextflowTypeDetailsHandler;
import org.ga4gh.starterkit.wes.utils.runmanager.detailshandler.type.RunTypeDetailsHandler;

public class RunManagerFactory {

    private static HashMap<WorkflowType, Class<? extends RunTypeDetailsHandler>> typeSetupClasses = new HashMap<>(){{
        put(WorkflowType.NEXTFLOW, NextflowTypeDetailsHandler.class);
    }};

    private static HashMap<WorkflowEngine, Class<? extends RunEngineDetailsHandler>> engineSetupClasses = new HashMap<>(){{
        put(WorkflowEngine.NATIVE, NativeEngineDetailsHandler.class);
    }};

    public RunManager createRunLauncher(WesRun wesRun) {
        try {
            RunManager runLauncher = new RunManager(wesRun);
            RunTypeDetailsHandler runTypeDetailsHandler = typeSetupClasses.get(wesRun.getWorkflowType()).getDeclaredConstructor().newInstance();
            RunEngineDetailsHandler runEngineDetailsHandler = engineSetupClasses.get(wesRun.getWorkflowEngine()).getDeclaredConstructor().newInstance();
            runTypeDetailsHandler.setWesRun(wesRun);
            runTypeDetailsHandler.setRunEngineDetailsHandler(runEngineDetailsHandler);
            runEngineDetailsHandler.setWesRun(wesRun);
            runEngineDetailsHandler.setRunTypeDetailsHandler(runTypeDetailsHandler);
            runLauncher.setRunTypeDetailsHandler(runTypeDetailsHandler);
            runLauncher.setRunEngineDetailsHandler(runEngineDetailsHandler);
            return runLauncher;
        } catch (NoSuchMethodException | InvocationTargetException |
            InstantiationException | IllegalAccessException ex) {
            return null;
        }
    }
}
