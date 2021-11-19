package org.ga4gh.starterkit.wes.config.engine;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import org.ga4gh.starterkit.wes.model.WorkflowEngine;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "type",
    defaultImpl = NativeEngineConfig.class,
    visible = true
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = NativeEngineConfig.class, name = "NATIVE"),
    @JsonSubTypes.Type(value = SlurmEngineConfig.class, name = "SLURM")
})
public abstract class EngineConfig {

    private WorkflowEngine type;

    public void setType(WorkflowEngine type) {
        this.type = type;
    }

    public WorkflowEngine getType() {
        return type;
    }
}
