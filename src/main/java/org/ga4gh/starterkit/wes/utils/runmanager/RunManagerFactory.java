package org.ga4gh.starterkit.wes.utils.runmanager;

import java.util.HashMap;
import org.ga4gh.starterkit.wes.model.WesRun;
import org.ga4gh.starterkit.wes.model.WorkflowEngine;
import org.ga4gh.starterkit.wes.model.WorkflowType;
import org.ga4gh.starterkit.wes.utils.runmanager.engine.NativeEngineHandler;
import org.ga4gh.starterkit.wes.utils.runmanager.engine.EngineHandler;
import org.ga4gh.starterkit.wes.utils.runmanager.language.NextflowLanguageHandler;
import org.ga4gh.starterkit.wes.utils.runmanager.language.LanguageHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Spring context-aware singleton that creates and configures RunManagers
 * via the application context
 */
public class RunManagerFactory implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    private static HashMap<WorkflowType, Class<? extends LanguageHandler>> typeSetupClasses = new HashMap<>(){{
        put(WorkflowType.NEXTFLOW, NextflowLanguageHandler.class);
    }};

    private static HashMap<WorkflowEngine, Class<? extends EngineHandler>> engineSetupClasses = new HashMap<>(){{
        put(WorkflowEngine.NATIVE, NativeEngineHandler.class);
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
        LanguageHandler runTypeDetailsHandler = applicationContext.getBean(typeSetupClasses.get(wesRun.getWorkflowType()));
        EngineHandler runEngineDetailsHandler = applicationContext.getBean(engineSetupClasses.get(wesRun.getWorkflowEngine()));
        runTypeDetailsHandler.setWesRun(wesRun);
        runTypeDetailsHandler.setRunEngineDetailsHandler(runEngineDetailsHandler);
        runEngineDetailsHandler.setWesRun(wesRun);
        runEngineDetailsHandler.setRunTypeDetailsHandler(runTypeDetailsHandler);
        runManager.setRunTypeDetailsHandler(runTypeDetailsHandler);
        runManager.setRunEngineDetailsHandler(runEngineDetailsHandler);
        runTypeDetailsHandler.setup();
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
