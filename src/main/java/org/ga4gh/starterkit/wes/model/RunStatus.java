package org.ga4gh.starterkit.wes.model;

public class RunStatus {

    private String runId;

    private State state;

    public RunStatus() {

    }

    public RunStatus(String runId, State state) {
        this.runId = runId;
        this.state = state;
    }

    public void setRunId(String runId) {
        this.runId = runId;
    }

    public String getRunId() {
        return runId;
    }

    public void setState(State state) {
        this.state = state;
    }

    public State getState() {
        return state;
    }
}
