package org.ga4gh.starterkit.wes.config.engine;

import org.ga4gh.starterkit.wes.model.ContainerizationType;

public abstract class AbstractEngineConfig implements EngineConfig {

    private String version;
    private ContainerizationType containerizationType;

    public AbstractEngineConfig() {
        setContainerizationType(ContainerizationType.DOCKER);
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public void setContainerizationType(ContainerizationType containerizationType) {
        this.containerizationType = containerizationType;
    }

    public ContainerizationType getContainerizationType() {
        return containerizationType;
    }
}
