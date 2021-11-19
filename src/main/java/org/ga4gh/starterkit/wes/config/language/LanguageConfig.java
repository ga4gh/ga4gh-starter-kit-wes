package org.ga4gh.starterkit.wes.config.language;

import org.ga4gh.starterkit.wes.config.engine.EngineConfig;

public class LanguageConfig {

    private boolean enabled;

    private EngineConfig engineConfig;

    public LanguageConfig() {

    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public void setEngineConfig(EngineConfig engineConfig) {
        this.engineConfig = engineConfig;
    }

    public EngineConfig getEngineConfig() {
        return engineConfig;
    }
}
