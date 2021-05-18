package org.ga4gh.starterkit.wes.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class RunId {

    private String runId;

    public RunId() {

    }

    public RunId(String runId) {
        this.runId = runId;
    }

    public void setRunId(String runId) {
        this.runId = runId;
    }

    public String getRunId() {
        return runId;
    }
}
