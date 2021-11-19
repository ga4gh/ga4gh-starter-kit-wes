package org.ga4gh.starterkit.wes.config.engine;

import org.ga4gh.starterkit.wes.model.WorkflowEngine;

public class NativeEngineConfig extends EngineConfig {

    private String rundir;

    public NativeEngineConfig() {
        setType(WorkflowEngine.NATIVE);
        setRundir("wes_runs");
    }

    public void setRundir(String rundir) {
        this.rundir = rundir;
    }

    public String getRundir() {
        return rundir;
    }
}
