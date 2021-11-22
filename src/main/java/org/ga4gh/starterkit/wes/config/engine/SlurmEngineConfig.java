package org.ga4gh.starterkit.wes.config.engine;

import org.ga4gh.starterkit.wes.config.compatibility.CanRunNextflow;
import org.ga4gh.starterkit.wes.model.WorkflowEngine;

public class SlurmEngineConfig extends AbstractEngineConfig implements CanRunNextflow {

    private String rundir;

    public SlurmEngineConfig() {
        setType(WorkflowEngine.SLURM);
        setRundir("wes_runs");
        // setVersion("0.1.0");
    }

    public void setRundir(String rundir) {
        this.rundir = rundir;
    }

    public String getRundir() {
        return rundir;
    }
}
