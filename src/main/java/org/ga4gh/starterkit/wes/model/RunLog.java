package org.ga4gh.starterkit.wes.model;

import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

/**
 * Directly from WES specification, contains complete information associated with
 * a workflow run, including supplied parameters, status, log information, and
 * outputs
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RunLog {

    /**
     * Run identifier
     */
    private String runId;

    /**
     * Initial run request parameters
     */
    private WesRequest request;

    /**
     * Workflow state/status
     */
    private State state;

    /**
     * Holistic workflow run-level log information
     */
    private WesLog runLog;

    /**
     * Task-level log information
     */
    private List<WesLog> taskLogs;

    /**
     * References to output file locations produced during the workflow run
     */
    private Map<String, String> outputs;

    /**
     * Instantiates a new RunLog object
     */
    public RunLog() {

    }

    /* Setters and getters */

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
     * Assign request
     * @param request workflow run request parameters
     */
    public void setRequest(WesRequest request) {
        this.request = request;
    }

    /**
     * Retrieve request
     * @return workflow run request parameters
     */
    public WesRequest getRequest() {
        return request;
    }

    /**
     * Assign state
     * @param state workflow run state
     */
    public void setState(State state) {
        this.state = state;
    }

    /**
     * Retrieve state
     * @return workflow run state
     */
    public State getState() {
        return state;
    }

    /**
     * Assign runLog
     * @param runLog workflow run-level log information
     */
    public void setRunLog(WesLog runLog) {
        this.runLog = runLog;
    }

    /**
     * Retrieve runLog
     * @return workflow run-level log information
     */
    public WesLog getRunLog() {
        return runLog;
    }

    /**
     * Assign taskLogs
     * @param taskLogs list of task-level log info (one task per item)
     */
    public void setTaskLogs(List<WesLog> taskLogs) {
        this.taskLogs = taskLogs;
    }

    /**
     * Retrieve taskLogs
     * @return list of task-level log info (one task per item)
     */
    public List<WesLog> getTaskLogs() {
        return taskLogs;
    }

    /**
     * Assign outputs
     * @param outputs map/dictionary of output file locations
     */
    public void setOutputs(Map<String, String> outputs) {
        this.outputs = outputs;
    }

    /**
     * Retrieve outputs
     * @return map/dictionary of output file locations
     */
    public Map<String, String> getOutputs() {
        return outputs;
    }
}
