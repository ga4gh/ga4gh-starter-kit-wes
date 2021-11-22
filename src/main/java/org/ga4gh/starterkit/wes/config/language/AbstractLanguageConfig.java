package org.ga4gh.starterkit.wes.config.language;

import java.util.List;

import org.ga4gh.starterkit.wes.model.WorkflowEngine;

public abstract class AbstractLanguageConfig implements LanguageConfig {

    private boolean enabled;

    private List<String> versions;

    private WorkflowEngine engine;

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public void setVersions(List<String> versions) {
        this.versions = versions;
    }

    public List<String> getVersions() {
        return versions;
    }

    public void setEngine(WorkflowEngine engine) {
        this.engine = engine;
    }

    public WorkflowEngine getEngine() {
        return engine;
    }
}
