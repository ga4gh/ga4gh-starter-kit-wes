package org.ga4gh.starterkit.wes.config.engine;

import org.ga4gh.starterkit.wes.model.WorkflowEngine;

public class NativeEngineConfig extends FilesystemEngineConfig {

    public NativeEngineConfig() {
        super();
        setRundir("wes_runs");
        setVersion("1.0.0");
    }

    public WorkflowEngine getType() {
        return WorkflowEngine.NATIVE;
    }
}
