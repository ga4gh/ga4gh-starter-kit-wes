package org.ga4gh.starterkit.wes.model;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class RunLog {

    private String runId;
    private WesRequest request;
    private State state;
    private WesLog runLog;
    private List<WesLog> taskLogs;
    private Map<String, String> outputs;

    public RunLog() {

    }

    /* Setters and getters */

    public void setRunId(String runId) {
        this.runId = runId;
    }

    public String getRunId() {
        return runId;
    }

    public void setRequest(WesRequest request) {
        this.request = request;
    }

    public WesRequest getRequest() {
        return request;
    }

    public void setState(State state) {
        this.state = state;
    }

    public State getState() {
        return state;
    }

    public void setRunLog(WesLog runLog) {
        this.runLog = runLog;
    }

    public WesLog getRunLog() {
        return runLog;
    }

    public void setTaskLogs(List<WesLog> taskLogs) {
        this.taskLogs = taskLogs;
    }

    public List<WesLog> getTaskLogs() {
        return taskLogs;
    }

    public void setOutputs(Map<String, String> outputs) {
        this.outputs = outputs;
    }

    public Map<String, String> getOutputs() {
        return outputs;
    }
}
