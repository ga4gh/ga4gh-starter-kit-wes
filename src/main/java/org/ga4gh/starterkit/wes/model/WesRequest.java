package org.ga4gh.starterkit.wes.model;

import java.util.Map;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

/**
 * Inferred from WES specification, contains original submission parameters from
 * a workflow run request. Known as 'RunRequest' in WES spec.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class WesRequest {

    /**
     * Key/value mapping of workflow input parameters
     */
    private Map workflowParams;

    /**
     * Workflow language specification (e.g. CWL, WDL, Nextflow)
     */
    private WorkflowType workflowType;

    /**
     * Workflow language specification version
     */
    private String workflowTypeVersion;

    /**
     * Key/value mapping of tags
     */
    private Map tags;

    /**
     * Key/value mapping of workflow engine-specific parameters
     */
    private Map workflowEngineParameters;

    /**
     * URL to workflow source
     */
    private String workflowUrl;


    /**
     * Instantiates a new WesRequest object
     */
    public WesRequest() {

    }

    /* Setters and getters */

    /**
     * Assign workflowParams
     * @param workflowParams Key/value mapping of workflow input parameters
     */
    public void setWorkflowParams(Map workflowParams) {
        this.workflowParams = workflowParams;
    }

    /**
     * Retrieve workflowParams
     * @return Key/value mapping of workflow input parameters
     */
    public Map getWorkflowParams() {
        return workflowParams;
    }

    /**
     * Assign workflowType
     * @param workflowType Workflow language specification (eg CWL, WDL, Nextflow)
     */
    public void setWorkflowType(WorkflowType workflowType) {
        this.workflowType = workflowType;
    }

    /**
     * Retrieve workflowType
     * @return Workflow language specification (eg CWL, WDL, Nextflow)
     */
    public WorkflowType getWorkflowType() {
        return workflowType;
    }

    /**
     * Assign workflowTypeVersion
     * @param workflowTypeVersion Workflow language specification version
     */
    public void setWorkflowTypeVersion(String workflowTypeVersion) {
        this.workflowTypeVersion = workflowTypeVersion;
    }

    /**
     * Retrieve workflowTypeVersion
     * @return Workflow language specification version
     */
    public String getWorkflowTypeVersion() {
        return workflowTypeVersion;
    }

    /**
     * Assign tags
     * @param tags Key/value mapping of tags
     */
    public void setTags(Map tags) {
        this.tags = tags;
    }

    /**
     * Retrieve tags
     * @return Key/value mapping of tags
     */
    public Map getTags() {
        return tags;
    }

    /**
     * Assign workflowEngineParameters
     * @param workflowEngineParameters Key/value mapping of workflow engine-specific parameters
     */
    public void setWorkflowEngineParameters(Map workflowEngineParameters) {
        this.workflowEngineParameters = workflowEngineParameters;
    }

    /**
     * Retrieve workflowEngineParameters
     * @return Key/value mapping of workflow engine-specific parameters
     */
    public Map getWorkflowEngineParameters() {
        return workflowEngineParameters;
    }

    /**
     * Assign workflowUrl
     * @param workflowUrl URL to workflow source
     */
    public void setWorkflowUrl(String workflowUrl) {
        this.workflowUrl = workflowUrl;
    }

    /**
     * Retrieve workflowUrl
     * @return URL to workflow source
     */
    public String getWorkflowUrl() {
        return workflowUrl;
    }
}
