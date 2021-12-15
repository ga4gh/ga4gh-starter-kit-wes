package org.ga4gh.starterkit.wes.config.engine;

public abstract class AbstractEngineConfig implements EngineConfig {

    private String version;

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }
}
