package org.ga4gh.starterkit.wes.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

/**
 * Directly from WES specification, contains only the id of a workflow run.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class RunId {

    /**
     * Workflow run identifier
     */
    private String runId;

    /**
     * Instantiates a new RunId
     */
    public RunId() {

    }

    /**
     * Instantiates a new RunId with an existing identifier
     * @param runId existing run identifier
     */
    public RunId(String runId) {
        this.runId = runId;
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
     * @return run identifer
     */
    public String getRunId() {
        return runId;
    }
}
