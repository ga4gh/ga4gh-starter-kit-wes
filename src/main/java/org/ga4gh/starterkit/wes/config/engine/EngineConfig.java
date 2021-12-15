package org.ga4gh.starterkit.wes.config.engine;

import org.ga4gh.starterkit.wes.model.ContainerizationType;
import org.ga4gh.starterkit.wes.model.WorkflowEngine;

public interface EngineConfig {

    public WorkflowEngine getType();
    public String getVersion();
    public ContainerizationType getContainerizationType();
}
