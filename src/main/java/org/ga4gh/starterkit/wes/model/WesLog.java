package org.ga4gh.starterkit.wes.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class WesLog {

    private String name;
    private List<String> cmd;
    private String startTime;
    private String endTime;
    private String stdout;
    private String stderr;
    private int exitCode;

    public WesLog() {

    }

    /* Setters and getters */

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setCmd(List<String> cmd) {
        this.cmd = cmd;
    }

    public List<String> getCmd() {
        return cmd;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setStdout(String stdout) {
        this.stdout = stdout;
    }

    public String getStdout() {
        return stdout;
    }

    public void setStderr(String stderr) {
        this.stderr = stderr;
    }

    public String getStderr() {
        return stderr;
    }

    public void setExitCode(int exitCode) {
        this.exitCode = exitCode;
    }

    public int getExitCode() {
        return exitCode;
    }
}
