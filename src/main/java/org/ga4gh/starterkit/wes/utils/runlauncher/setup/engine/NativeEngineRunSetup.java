package org.ga4gh.starterkit.wes.utils.runlauncher.setup.engine;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.ga4gh.starterkit.wes.model.WesRun;

public class NativeEngineRunSetup implements WorkflowEngineRunSetup {

    private static final Path wesRunDir = Paths.get("wes_runs");

    private WesRun wesRun;
    private Path jobDirectory;
    private String[] command;
    private String[] wrappedCommand;

    public NativeEngineRunSetup() {
        
    }

    public void stageWorkingArea() throws Exception {
        setJobDirectory();
        Path paramsFile = Paths.get(jobDirectory.toString(), "params.json");
        Files.createDirectories(jobDirectory);
        FileUtils.writeStringToFile(paramsFile.toFile(), wesRun.getWorkflowParams(), "utf-8");
    }

    public void setWorkflowCommand(String[] command) {
        this.command = command;
    }

    public void wrapWorkflowCommand() {
        wrappedCommand = command;
    }

    public void launchWrappedWorkflowCommand() throws Exception {
        new ProcessBuilder()
            .command(wrappedCommand)
            .directory(jobDirectory.toFile())
            .start();
    }

    /* Private convenience methods */

    private void setJobDirectory() throws Exception {
        String id = getWesRun().getId();
        Path path = Paths.get(
            wesRunDir.toString(),
            id.substring(0, 2),
            id.substring(2, 4),
            id.substring(4, 6),
            id
        );
        jobDirectory = path;
    }

    /* Setters and getters */

    public void setWesRun(WesRun wesRun) {
        this.wesRun = wesRun;
    }

    public WesRun getWesRun() {
        return wesRun;
    }
}
