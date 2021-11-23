package org.ga4gh.starterkit.wes.config.engine;

import org.ga4gh.starterkit.wes.model.WorkflowEngine;

public class SlurmEngineConfig extends AbstractEngineConfig {

    private String rundir;

    public SlurmEngineConfig() {
        setRundir("wes_runs");
        setVersion("0.1.0");
    }

    public WorkflowEngine getType() {
        return WorkflowEngine.SLURM;
    }

    public void setRundir(String rundir) {
        this.rundir = rundir;
    }

    public String getRundir() {
        return rundir;
    }
}
