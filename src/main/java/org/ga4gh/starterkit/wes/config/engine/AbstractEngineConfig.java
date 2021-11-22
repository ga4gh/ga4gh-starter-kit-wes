package org.ga4gh.starterkit.wes.config.engine;

import org.ga4gh.starterkit.wes.model.WorkflowEngine;

public abstract class AbstractEngineConfig implements EngineConfig {

    private WorkflowEngine type;

    // private String version;

    public void setType(WorkflowEngine type) {
        this.type = type;
    }

    public WorkflowEngine getType() {
        return type;
    }

    /*
    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }
    */
}
