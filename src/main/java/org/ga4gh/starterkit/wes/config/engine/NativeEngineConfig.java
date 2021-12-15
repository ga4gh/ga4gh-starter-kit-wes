package org.ga4gh.starterkit.wes.config.engine;

import org.ga4gh.starterkit.wes.model.WorkflowEngine;

public class NativeEngineConfig extends AbstractEngineConfig {

    private String rundir;

    public NativeEngineConfig() {
        super();
        setRundir("wes_runs");
        setVersion("1.0.0");
    }

    public WorkflowEngine getType() {
        return WorkflowEngine.NATIVE;
    }

    public void setRundir(String rundir) {
        this.rundir = rundir;
    }

    public String getRundir() {
        return rundir;
    }
}
