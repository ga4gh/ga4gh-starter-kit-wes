package org.ga4gh.starterkit.wes.utils.runmanager;

import java.util.HashMap;
import org.ga4gh.starterkit.wes.config.WesServiceProps;
import org.ga4gh.starterkit.wes.config.engine.EngineConfig;
import org.ga4gh.starterkit.wes.config.language.LanguageConfig;
import org.ga4gh.starterkit.wes.model.WesRun;
import org.ga4gh.starterkit.wes.model.WorkflowEngine;
import org.ga4gh.starterkit.wes.model.WorkflowType;
import org.ga4gh.starterkit.wes.utils.runmanager.engine.NativeEngineHandler;
import org.ga4gh.starterkit.wes.utils.runmanager.engine.SlurmEngineHandler;
import org.ga4gh.starterkit.wes.utils.runmanager.engine.EngineHandler;
import org.ga4gh.starterkit.wes.utils.runmanager.language.NextflowLanguageHandler;
import org.ga4gh.starterkit.wes.utils.runmanager.language.WdlLanguageHandler;
import org.ga4gh.starterkit.wes.utils.runmanager.language.LanguageHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Spring context-aware singleton that creates and configures RunManagers
 * via the application context
 */
public class RunManagerFactory implements ApplicationContextAware {

    @Autowired
    private WesServiceProps wesServiceProps;

    private ApplicationContext applicationContext;

    private static HashMap<WorkflowType, Class<? extends LanguageHandler>> typeSetupClasses = new HashMap<>(){{
        put(WorkflowType.NEXTFLOW, NextflowLanguageHandler.class);
        put(WorkflowType.WDL, WdlLanguageHandler.class);
    }};

    private static HashMap<WorkflowEngine, Class<? extends EngineHandler>> engineSetupClasses = new HashMap<>(){{
        put(WorkflowEngine.NATIVE, NativeEngineHandler.class);
        put(WorkflowEngine.SLURM, SlurmEngineHandler.class);
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
        LanguageConfig languageConfig = getWesServiceProps().getLanguageConfig(wesRun.getWorkflowType());
        EngineConfig engineConfig = getWesServiceProps().getEngineConfigForLanguage(wesRun.getWorkflowType());

        RunManager runManager = applicationContext.getBean(RunManager.class);
        
        LanguageHandler languageHandler = applicationContext.getBean(typeSetupClasses.get(wesRun.getWorkflowType()));
        EngineHandler engineHandler = applicationContext.getBean(engineSetupClasses.get(engineConfig.getType()));
        
        languageHandler.setWesRun(wesRun);
        languageHandler.setLanguageConfig(languageConfig);
        languageHandler.setEngineHandler(engineHandler);
        
        engineHandler.setWesRun(wesRun);
        engineHandler.setEngineConfig(engineConfig);
        engineHandler.setLanguageHandler(languageHandler);
        
        runManager.setWesRun(wesRun);
        runManager.setLanguageHandler(languageHandler);
        runManager.setEngineHandler(engineHandler);

        languageHandler.setup();
        engineHandler.setup();
        return runManager;
    }

    public void setWesServiceProps(WesServiceProps wesServiceProps) {
        this.wesServiceProps = wesServiceProps;
    }

    public WesServiceProps getWesServiceProps() {
        return wesServiceProps;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
