package org.ga4gh.starterkit.wes.config.engine;

import org.ga4gh.starterkit.wes.model.WorkflowEngine;

public class NativeEngineConfig extends AbstractEngineConfig {

    private String rundir;

    public NativeEngineConfig() {
        setType(WorkflowEngine.NATIVE);
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
