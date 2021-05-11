package org.ga4gh.starterkit.wes.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.ga4gh.starterkit.common.hibernate.HibernateEntity;

@Entity
@Table(name = "wes_run")
public class WesRun implements HibernateEntity<String> {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "workflow_type")
    private WorkflowType workflowType;

    @Column(name = "workflow_type_version")
    private String workflowTypeVersion;

    @Column(name = "workflow_url")
    private String workflowUrl;

    @Column(name = "workflow_params")
    private String workflowParams;

    @Column(name = "workflow_engine")
    private WorkflowEngine workflowEngine;

    @Column(name = "workflow_engine_version")
    private String workflowEngineVersion;

    public WesRun() {

    }

    public void loadRelations() {

    }

    public RunId toRunId() {
        return new RunId(id);
    }

    // Setters and getters

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
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

    public void setWorkflowUrl(String workflowUrl) {
        this.workflowUrl = workflowUrl;
    }

    public String getWorkflowUrl() {
        return workflowUrl;
    }

    public void setWorkflowParams(String workflowParams) {
        this.workflowParams = workflowParams;
    }

    public String getWorkflowParams() {
        return workflowParams;
    }

    public void setWorkflowEngine(WorkflowEngine workflowEngine) {
        this.workflowEngine = workflowEngine;
    }

    public WorkflowEngine getWorkflowEngine() {
        return workflowEngine;
    }

    public void setWorkflowEngineVersion(String workflowEngineVersion) {
        this.workflowEngineVersion = workflowEngineVersion;
    }

    public String getWorkflowEngineVersion() {
        return workflowEngineVersion;
    }
}
