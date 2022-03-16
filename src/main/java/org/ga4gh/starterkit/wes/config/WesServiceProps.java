package org.ga4gh.starterkit.wes.config;


import org.ga4gh.starterkit.wes.config.engine.EngineConfig;
import org.ga4gh.starterkit.wes.config.language.LanguageConfig;
import org.ga4gh.starterkit.wes.model.WorkflowEngine;
import org.ga4gh.starterkit.wes.model.WorkflowType;

public class WesServiceProps {

    private WesServicePropsLanguages languages;
    private WesServicePropsEngines engines;

    public WesServiceProps() {
        languages = new WesServicePropsLanguages();
        engines = new WesServicePropsEngines();
    }

    public LanguageConfig getLanguageConfig(WorkflowType workflowType) {
        LanguageConfig languageConfig = null;
        switch (workflowType) {
            case NEXTFLOW:
                languageConfig = getLanguages().getNextflow();
                break;
            case WDL:
                languageConfig = getLanguages().getWdl();
                break;
            default:
                languageConfig = null;
                break;
        }
        return languageConfig;
    }

    public EngineConfig getEngineConfigForLanguage(WorkflowType workflowType) {
        EngineConfig engineConfig = null;
        LanguageConfig languageConfig = getLanguageConfig(workflowType);
        WorkflowEngine workflowEngine = languageConfig.getEngine();
        switch (workflowEngine) {
            case NATIVE:
                engineConfig = getEngines().getNativeConfig();
                break;
            case SLURM:
                engineConfig = getEngines().getSlurm();
                break;
            default:
                engineConfig = null;
                break;
        }
        return engineConfig;
    }

    public void setLanguages(WesServicePropsLanguages languages) {
        this.languages = languages;
    }

    public WesServicePropsLanguages getLanguages() {
        return languages;
    }

    public void setEngines(WesServicePropsEngines engines) {
        this.engines = engines;
    }

    public WesServicePropsEngines getEngines() {
        return engines;
    }
}
