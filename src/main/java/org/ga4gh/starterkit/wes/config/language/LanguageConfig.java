package org.ga4gh.starterkit.wes.config.language;

import org.ga4gh.starterkit.wes.config.engine.WorkflowEngineConfig;

public class LanguageConfig {

    private boolean enabled;

    private WorkflowEngineConfig workflowEngine;

    public LanguageConfig() {

    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public void setWorkflowEngine(WorkflowEngineConfig workflowEngine) {
        this.workflowEngine = workflowEngine;
    }

    public WorkflowEngineConfig getWorkflowEngine() {
        return workflowEngine;
    }
}
