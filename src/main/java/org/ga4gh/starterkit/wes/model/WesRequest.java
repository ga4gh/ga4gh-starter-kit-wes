package org.ga4gh.starterkit.wes.model;

import java.util.Map;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class WesRequest {

    private Map workflowParams;
    private WorkflowType workflowType;
    private String workflowTypeVersion;
    private Map tags;
    private Map workflowEngineParameters;
    private String workflowUrl;


    public WesRequest() {

    }

    /* Setters and getters */

    public void setWorkflowParams(Map workflowParams) {
        this.workflowParams = workflowParams;
    }

    public Map getWorkflowParams() {
        return workflowParams;
    }

    public void setWorkflowType(WorkflowType workflowType) {
        this.workflowType = workflowType;
    }

    public WorkflowType getWorkflowType() {
        return workflowType;
    }

    public void setWorkflowTypeVersion(String workflowTypeVersion) {
        this.workflowTypeVersion = workflowTypeVersion;
    }

    public String getWorkflowTypeVersion() {
        return workflowTypeVersion;
    }

    public void setTags(Map tags) {
        this.tags = tags;
    }

    public Map getTags() {
        return tags;
    }

    public void setWorkflowEngineParameters(Map workflowEngineParameters) {
        this.workflowEngineParameters = workflowEngineParameters;
    }

    public Map getWorkflowEngineParameters() {
        return workflowEngineParameters;
    }

    public void setWorkflowUrl(String workflowUrl) {
        this.workflowUrl = workflowUrl;
    }

    public String getWorkflowUrl() {
        return workflowUrl;
    }
}
