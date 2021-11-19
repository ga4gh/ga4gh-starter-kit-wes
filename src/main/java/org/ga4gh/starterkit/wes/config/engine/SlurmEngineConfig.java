package org.ga4gh.starterkit.wes.config.engine;

import org.ga4gh.starterkit.wes.config.compatibility.CanRunNextflow;
import org.ga4gh.starterkit.wes.model.WorkflowEngine;

public class SlurmEngineConfig extends EngineConfig implements CanRunNextflow {

    private String rundir;

    public SlurmEngineConfig() {
        setType(WorkflowEngine.SLURM);
        setRundir("wes_runs");
    }

    public void setRundir(String rundir) {
        this.rundir = rundir;
    }

    public String getRundir() {
        return rundir;
    }
}
