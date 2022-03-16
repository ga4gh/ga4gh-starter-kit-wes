package org.ga4gh.starterkit.wes.model;

import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ga4gh.starterkit.common.hibernate.HibernateEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Database entity not directly associated with WES specification, stores workflow
 * run requests (including request parameters) in the database
 */
@Entity
@Table(name = "wes_run")
@Setter
@Getter
@NoArgsConstructor
public class WesRun implements HibernateEntity<String> {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
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

    @Column(name = "cromwell_run_id")
    private String cromwellRunId;

    @Column(name = "final_run_log_json")
    private String finalRunLogJson;

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
}
