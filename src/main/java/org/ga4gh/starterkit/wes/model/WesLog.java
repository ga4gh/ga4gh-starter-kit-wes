package org.ga4gh.starterkit.wes.model;

import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

/**
 * Inferred from WES specification, common model for log information. Known as 
 * 'Log' in WES spec. Used by the RunLog for both workflow run-level log info (runLog 
 * property) and task-level log info (taskLogs property).
 * 
 * @see org.ga4gh.starterkit.wes.model.RunLog RunLog
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class WesLog {

    /**
     * Run/task name
     */
    private String name;

    /**
     * CLI commands issued by run/task 
     */
    private List<String> cmd;

    /**
     * Run/task start time
     */
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime startTime;

    /**
     * Run/task completion time
     */
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime endTime;

    /**
     * URL to retrieve run/task level stdout logs
     */
    private String stdout;

    /**
     * URL to retrieve run/task level stderr logs
     */
    private String stderr;

    /**
     * POSIX exit code for run/task
     */
    private int exitCode;

    /**
     * Instantiate a new WesLog object
     */
    public WesLog() {

    }

    /* Setters and getters */

    /**
     * Assign name
     * @param name run/task name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retrieve name
     * @return run/task name
     */
    public String getName() {
        return name;
    }

    /**
     * Assign cmd
     * @param cmd CLI commands issued by run/task
     */
    public void setCmd(List<String> cmd) {
        this.cmd = cmd;
    }

    /**
     * Retrieve cmd
     * @return CLI commands issued by run/task
     */
    public List<String> getCmd() {
        return cmd;
    }

    /**
     * Assign startTime
     * @param startTime run/task start time
     */
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    /**
     * Retrieve startTime
     * @return run/task start time
     */
    public LocalDateTime getStartTime() {
        return startTime;
    }

    /**
     * Assign endTime
     * @param endTime run/task completion time
     */
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    /**
     * Retrieve endTime
     * @return run/task completion time
     */
    public LocalDateTime getEndTime() {
        return endTime;
    }

    /**
     * Assign stdout
     * @param stdout URL to retrieve stdout logs
     */
    public void setStdout(String stdout) {
        this.stdout = stdout;
    }

    /**
     * Retrieve stdout
     * @return URL to retrieve stdout logs
     */
    public String getStdout() {
        return stdout;
    }

    /**
     * Assign stderr
     * @param stderr URL to retrieve stderr logs
     */
    public void setStderr(String stderr) {
        this.stderr = stderr;
    }

    /**
     * Retrieve stderr
     * @return URL to retrieve stderr logs
     */
    public String getStderr() {
        return stderr;
    }

    /**
     * Assign exitCode
     * @param exitCode POSIX exit code for run/task
     */
    public void setExitCode(int exitCode) {
        this.exitCode = exitCode;
    }

    /**
     * Retrieve exitCode
     * @return POSIX exit code for run/task
     */
    public int getExitCode() {
        return exitCode;
    }
}
