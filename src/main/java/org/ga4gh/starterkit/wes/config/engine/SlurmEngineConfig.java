package org.ga4gh.starterkit.wes.config.engine;

import org.ga4gh.starterkit.wes.model.WorkflowEngine;

public class SlurmEngineConfig extends FilesystemEngineConfig {

    public SlurmEngineConfig() {
        super();
        setRundir("wes_runs");
        setVersion("0.1.0");
    }

    public WorkflowEngine getType() {
        return WorkflowEngine.SLURM;
    }
}
