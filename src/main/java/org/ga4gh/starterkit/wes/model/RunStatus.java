package org.ga4gh.starterkit.wes.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

/**
 * Directly from WES specification, contains identifier and state for a workflow run 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RunStatus {

    /**
     * Run identifer
     */
    private String runId;

    /**
     * Run state
     */
    private State state;

    /**
     * Instantiates a new RunStatus object
     */
    public RunStatus() {

    }

    /**
     * Instantiates a new RunStatus object
     * @param runId run identifier
     * @param state run state
     */
    public RunStatus(String runId, State state) {
        this.runId = runId;
        this.state = state;
    }

    /**
     * Assign runId
     * @param runId run identifier
     */
    public void setRunId(String runId) {
        this.runId = runId;
    }

    /**
     * Retrieve runId
     * @return run identifier
     */
    public String getRunId() {
        return runId;
    }

    /**
     * Assign state
     * @param state run state
     */
    public void setState(State state) {
        this.state = state;
    }

    /**
     * Retrieve state
     * @return run state
     */
    public State getState() {
        return state;
    }
}
