package org.ga4gh.starterkit.wes.utils.runmanager.engine;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.io.FileUtils;

/**
 * Facilitates access to data/information for workflow runs launched via the 
 * 'Native' engine, that is, launched on the host machine without leveraging any
 * HPC or Cloud batch scheduling resources
 */
public class NativeEngineHandler extends AbstractEngineHandler {

    private static final Path wesRunDir = Paths.get("wes_runs");

    private Path jobDirectory;

    /**
     * Instantiates a new NativeEngineDetailsHandler instance
     */
    public NativeEngineHandler() {
        
    }

    // common operations

    public List<String> provideDirectoryContents(String directory) throws Exception {
        Path jobDirectory = getJobDirectory();
        Path dirPath = Path.of(directory);
        if (!new File(directory).isAbsolute()) {
            dirPath = Paths.get(jobDirectory.toString(), directory);
        }
        List<String> filesList = Arrays.asList(dirPath.toFile().list());
        return filesList;
    }

    public String getRequestedFileContents(String filename) throws Exception {
        Path jobDirectory = getJobDirectory();
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

    /**
     * Constructs an isolated directory for the workflow run, based on its id
     * @throws Exception a server-side error occurred
     */
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

    /**
     * Retrieves the path to the job directory, loading it if it hasn't already
     * been set
     * @return the job directory for the workflow run
     * @throws Exception a server-side error occurred
     */
    private Path getJobDirectory() throws Exception {
        if (jobDirectory == null) {
            setJobDirectory();
        }
        return jobDirectory;
    }
}
