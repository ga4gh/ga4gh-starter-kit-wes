package org.ga4gh.starterkit.wes.model;

import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ga4gh.starterkit.common.hibernate.HibernateEntity;

/**
 * Database entity not directly associated with WES specification, stores workflow
 * run requests (including request parameters) in the database
 */
@Entity
@Table(name = "wes_run")
public class WesRun implements HibernateEntity<String> {

    /**
     * Run identifier
     */
    @Id
    @Column(name = "id")
    private String id;

    /**
     * Workflow language specification (eg CWL, WDL Nextflow)
     */
    @Column(name = "workflow_type")
    private WorkflowType workflowType;

    /**
     * Workflow language specification version
     */
    @Column(name = "workflow_type_version")
    private String workflowTypeVersion;

    /**
     * URL to workflow source
     */
    @Column(name = "workflow_url")
    private String workflowUrl;

    /**
     * Raw JSON string of workflow input parameters
     */
    @Column(name = "workflow_params")
    private String workflowParams;

    /**
     * Engine/launch type
     */
    @Column(name = "workflow_engine")
    private WorkflowEngine workflowEngine;

    /**
     * Version of engine/launch type
     */
    @Column(name = "workflow_engine_version")
    private String workflowEngineVersion;

    /**
     * Instantiates a new WesRun object
     */
    public WesRun() {

    }

    /**
     * Loads any related entities that are not fetched eagerly (ie lazy fetching)
     */
    public void loadRelations() {

    }

    /**
     * Converts the WesRun to a simple RunId
     * @return Simplified RunId object
     */
    public RunId toRunId() {
        return new RunId(id);
    }

    /**
     * Converts the WesRun to a WesRequest
     * @return WesRequest object
     */
    public WesRequest toWesRequest() {
        WesRequest wesRequest = new WesRequest();
        wesRequest.setWorkflowType(getWorkflowType());
        wesRequest.setWorkflowTypeVersion(getWorkflowTypeVersion());
        wesRequest.setWorkflowUrl(getWorkflowUrl());

        // deserialize workflowParams JSON string to a map 
        ObjectMapper mapper = new ObjectMapper();
        try {
            Map workflowParams = mapper.readValue(getWorkflowParams(), Map.class);
            wesRequest.setWorkflowParams(workflowParams);
        } catch (Exception ex) {
            // TODO better exception handling of malformed JSON
        }

        return wesRequest;
    }

    /* Setters and getters */

    /**
     * Assign id
     * @param id Run identifier
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Retrieve id
     * @return Run identifier
     */
    public String getId() {
        return id;
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

    /**
     * Assign workflowParams
     * @param workflowParams Raw JSON string of workflow input parameters
     */
    public void setWorkflowParams(String workflowParams) {
        this.workflowParams = workflowParams;
    }

    /**
     * Retrieve workflowParams
     * @return Raw JSON string of workflow input parameters
     */
    public String getWorkflowParams() {
        return workflowParams;
    }

    /**
     * Assign workflowEngine
     * @param workflowEngine Engine/launch type
     */
    public void setWorkflowEngine(WorkflowEngine workflowEngine) {
        this.workflowEngine = workflowEngine;
    }

    /**
     * Retrieve workflowEngine
     * @return Engine/launch type
     */
    public WorkflowEngine getWorkflowEngine() {
        return workflowEngine;
    }

    /**
     * Assign workflowEngineVersion
     * @param workflowEngineVersion Version of engine/launch type
     */
    public void setWorkflowEngineVersion(String workflowEngineVersion) {
        this.workflowEngineVersion = workflowEngineVersion;
    }

    /**
     * Retrieve workflowEngineVersion
     * @return Version of engine/launch type
     */
    public String getWorkflowEngineVersion() {
        return workflowEngineVersion;
    }
}
