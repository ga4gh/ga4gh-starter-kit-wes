package org.ga4gh.starterkit.wes.config.language;

import java.util.List;

import org.ga4gh.starterkit.wes.config.engine.EngineConfig;

public abstract class AbstractLanguageConfig implements LanguageConfig {

    private boolean enabled;

    private List<String> versions;

    private EngineConfig engineConfig;

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

    public void setEngineConfig(EngineConfig engineConfig) {
        this.engineConfig = engineConfig;
    }

    public EngineConfig getEngineConfig() {
        return engineConfig;
    }
}
