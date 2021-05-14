package org.ga4gh.starterkit.wes.utils.runmanager.detailshandler.engine;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.io.FileUtils;

public class NativeEngineDetailsHandler extends AbstractRunEngineDetailsHandler {

    private static final Path wesRunDir = Paths.get("wes_runs");

    private Path jobDirectory;

    public NativeEngineDetailsHandler() {
        
    }

    // common operations

    public List<String> provideDirectoryContents(String directory) throws Exception {
        Path jobDirectory = getJobDirectory();
        Path dirPath = Path.of(directory);
        if (!new File(directory).isAbsolute()) {
            dirPath = Paths.get(jobDirectory.toString(), directory);
        }
        return Arrays.asList(dirPath.toFile().list());
    }

    public String getRequestedFileContents(String filename) throws Exception {
        Path filePath = Path.of(filename);
        if (!new File(filename).isAbsolute()) {
            filePath = Paths.get(jobDirectory.toString(), filename);
        }
        return new String ( Files.readAllBytes(filePath));
    }

    public String getRequestedCommandStdout(String[] command) throws Exception {
        Process process = new ProcessBuilder()
            .command(command)
            .directory(getJobDirectory().toFile())
            .start();
        String stdout = new String (process.getInputStream().readAllBytes());
        return stdout;
    }

    // for launching workflow runs

    public void stageWorkingArea() throws Exception {
        setJobDirectory();
        Path paramsFile = Paths.get(jobDirectory.toString(), "params.json");
        Files.createDirectories(jobDirectory);
        FileUtils.writeStringToFile(paramsFile.toFile(), getWesRun().getWorkflowParams(), "utf-8");
    }

    public void launchWorkflowRunCommand(String[] workflowRunCommand) throws Exception {
        new ProcessBuilder()
            .command(workflowRunCommand)
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

    private Path getJobDirectory() throws Exception {
        if (jobDirectory == null) {
            setJobDirectory();
        }
        return jobDirectory;
    }
}
