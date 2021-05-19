package org.ga4gh.starterkit.wes.utils.runmanager;

import java.util.HashMap;
import org.ga4gh.starterkit.wes.model.WesRun;
import org.ga4gh.starterkit.wes.model.WorkflowEngine;
import org.ga4gh.starterkit.wes.model.WorkflowType;
import org.ga4gh.starterkit.wes.utils.runmanager.detailshandler.engine.NativeEngineDetailsHandler;
import org.ga4gh.starterkit.wes.utils.runmanager.detailshandler.engine.RunEngineDetailsHandler;
import org.ga4gh.starterkit.wes.utils.runmanager.detailshandler.type.NextflowTypeDetailsHandler;
import org.ga4gh.starterkit.wes.utils.runmanager.detailshandler.type.RunTypeDetailsHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class RunManagerFactory implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    private static HashMap<WorkflowType, Class<? extends RunTypeDetailsHandler>> typeSetupClasses = new HashMap<>(){{
        put(WorkflowType.NEXTFLOW, NextflowTypeDetailsHandler.class);
    }};

    private static HashMap<WorkflowEngine, Class<? extends RunEngineDetailsHandler>> engineSetupClasses = new HashMap<>(){{
        put(WorkflowEngine.NATIVE, NativeEngineDetailsHandler.class);
    }};

    public RunManager createRunLauncher(WesRun wesRun) {
        
        RunManager runManager = applicationContext.getBean(RunManager.class);
        RunTypeDetailsHandler runTypeDetailsHandler = applicationContext.getBean(typeSetupClasses.get(wesRun.getWorkflowType()));
        RunEngineDetailsHandler runEngineDetailsHandler = applicationContext.getBean(engineSetupClasses.get(wesRun.getWorkflowEngine()));
        runTypeDetailsHandler.setWesRun(wesRun);
        runTypeDetailsHandler.setRunEngineDetailsHandler(runEngineDetailsHandler);
        runEngineDetailsHandler.setWesRun(wesRun);
        runEngineDetailsHandler.setRunTypeDetailsHandler(runTypeDetailsHandler);
        runManager.setRunTypeDetailsHandler(runTypeDetailsHandler);
        runManager.setRunEngineDetailsHandler(runEngineDetailsHandler);
        return runManager;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
