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

/**
 * Spring context-aware singleton that creates and configures RunManagers
 * via the application context
 */
public class RunManagerFactory implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    private static HashMap<WorkflowType, Class<? extends RunTypeDetailsHandler>> typeSetupClasses = new HashMap<>(){{
        put(WorkflowType.NEXTFLOW, NextflowTypeDetailsHandler.class);
    }};

    private static HashMap<WorkflowEngine, Class<? extends RunEngineDetailsHandler>> engineSetupClasses = new HashMap<>(){{
        put(WorkflowEngine.NATIVE, NativeEngineDetailsHandler.class);
    }};

    /**
     * Instantiates a new RunManager instance with the correct workflow type
     * and workflow engine handlers according to the WesRun of interest
     * @param wesRun WesRun entity for a particular workflow run
     * @return configured RunManager instance
     */
    public RunManager createRunManager(WesRun wesRun) {
        // load the RunManager, the correct RunTypeDetailsHandler child class
        // according to workflowType, and the correct RunEngineDetailsHandler
        // according to workflowEngine
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

    /**
     * Assigns applicationContext
     * @param applicationContext Spring application context
     */
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
