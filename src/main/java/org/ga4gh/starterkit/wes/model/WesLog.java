package org.ga4gh.starterkit.wes.model;

import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class WesLog {

    private String name;
    private List<String> cmd;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime startTime;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime endTime;

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

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public LocalDateTime getEndTime() {
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
