package org.ga4gh.starterkit.wes.config;


import org.ga4gh.starterkit.wes.config.engine.EngineConfig;
import org.ga4gh.starterkit.wes.config.language.LanguageConfig;
import org.ga4gh.starterkit.wes.model.WorkflowEngine;
import org.ga4gh.starterkit.wes.model.WorkflowType;

public class WesServiceProps {

    private WesServicePropsLanguages languages;
    private WesServicePropsEngines engines;
    private boolean drsDockerContainer;

    public WesServiceProps() {
        languages = new WesServicePropsLanguages();
        engines = new WesServicePropsEngines();
        // If wes and drs are both docker containers then localhost in drs object id would refer to wes container.
        // drsDockerContainer flag is used to set localhost to "host.docker.internal" which refers to the actual host machine.
        drsDockerContainer = false;
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

    public boolean getDrsDockerContainer() {
        return drsDockerContainer;
    }

    public void setDrsDockerContainer(boolean drsDockerContainer) {
        this.drsDockerContainer = drsDockerContainer;
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
