package org.ga4gh.starterkit.wes.config.engine;

import org.ga4gh.starterkit.wes.config.compatibility.CanRunNextflow;
import org.ga4gh.starterkit.wes.model.WorkflowEngine;

public class SlurmEngineConfig extends WorkflowEngineConfig implements CanRunNextflow {

    private String foo;

    public SlurmEngineConfig() {
        setType(WorkflowEngine.SLURM);
        setFoo("foo");
    }

    public void setFoo(String foo) {
        this.foo = foo;
    }

    public String getFoo() {
        return foo;
    }
}
