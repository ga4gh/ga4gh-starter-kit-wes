package org.ga4gh.starterkit.wes.config.engine;

import org.ga4gh.starterkit.wes.model.WorkflowEngine;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

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
public interface EngineConfig {

    public void setType(WorkflowEngine type);
    public WorkflowEngine getType();
    
}
