package org.ga4gh.starterkit.wes.config.language;

import org.ga4gh.starterkit.wes.config.engine.NativeEngineConfig;

public class NextflowLanguageConfig extends LanguageConfig {

    public NextflowLanguageConfig() {
        setEnabled(true);
        setWorkflowEngine(new NativeEngineConfig());
    }
}
