package org.ga4gh.starterkit.wes.utils.runmanager.engine;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.ga4gh.starterkit.wes.config.engine.EngineConfig;
import org.ga4gh.starterkit.wes.config.engine.NativeEngineConfig;
import org.ga4gh.starterkit.wes.utils.runmanager.language.CommandOutput;

/**
 * Facilitates access to data/information for workflow runs launched via the 
 * 'Native' engine, that is, launched on the host machine without leveraging any
 * HPC or Cloud batch scheduling resources
 */
public class NativeEngineHandler extends AbstractEngineHandler {

    private NativeEngineConfig engineConfig;
    private Path jobDirectory;

    public NativeEngineHandler() {
        
    }

    // common operations

    @Override
    public void setup() {
        setJobDirectory(constructJobDirectory());
    }

    public List<String> provideDirectoryContents(String directory) {
        Path jobDirectory = getJobDirectory();
        Path dirPath = Path.of(directory);
        if (!new File(directory).isAbsolute()) {
            dirPath = Paths.get(jobDirectory.toString(), directory);
        }
        List<String> filesList = Arrays.asList(dirPath.toFile().list());
        return filesList;
    }

    public String getRequestedFileContents(String filename) {
        String contents = null;

        try {
            Path jobDirectory = getJobDirectory();
            Path filePath = Path.of(filename);
            if (!new File(filename).isAbsolute()) {
                filePath = Paths.get(jobDirectory.toString(), filename);
            }
            contents = new String (Files.readAllBytes(filePath));
        } catch (Exception ex) {
            setException(ex);
        }

        return contents;
    }

    @Override
    public FileMetadata provideFileAttributes(String filename) {
        FileMetadata fileMetadata = new FileMetadata();
        try {
            Path jobDirectory = getJobDirectory();
            Path filePath = Path.of(filename);
            if (!new File(filename).isAbsolute()) {
                filePath = Paths.get(jobDirectory.toString(), filename);
            }
            fileMetadata.setFilename(filePath.toString());
            fileMetadata.setFileAttributes(Files.readAttributes(filePath, BasicFileAttributes.class));
        } catch (Exception ex) {
            setException(ex);
        }
        return fileMetadata;
    }

    @Override
    public CommandOutput getRequestedCommandOutput(String[] command) {
        CommandOutput commandOutput = new CommandOutput();
        try {
            Process process = new ProcessBuilder()
                .command(command)
                .directory(getJobDirectory().toFile())
                .start();
            process.waitFor();
            commandOutput.setExitCode(process.exitValue());
            commandOutput.setStdout(new String (process.getInputStream().readAllBytes()));
            commandOutput.setStderr(new String (process.getErrorStream().readAllBytes()));
        } catch (Exception ex) {
            setException(ex);
        }

        return commandOutput;
    }

    // for launching workflow runs

    public void writeContentToFile(String filePath, String content) {
        try {
            Path outputFile = Paths.get(getJobDirectory().toString(), filePath);
            FileUtils.writeStringToFile(outputFile.toFile(), content, "utf-8");
        } catch (Exception ex) {
            setException(ex);
        }
    }

    public void stageWorkingArea() {
        try {
            Path paramsFile = Paths.get(getJobDirectory().toString(), "params.json");
            Files.createDirectories(getJobDirectory());
            FileUtils.writeStringToFile(paramsFile.toFile(), getWesRun().getWorkflowParams(), "utf-8");
        } catch (Exception ex) {
            setException(ex);
        }
    }

    public void launchWorkflowRunCommand(String[] workflowRunCommand) {
        try {
            new ProcessBuilder()
                .command(workflowRunCommand)
                .directory(jobDirectory.toFile())
                .start();
        } catch (Exception ex) {
            setException(ex);
        }
    }

    /* Private convenience methods */

    /**
     * Constructs an isolated directory for the workflow run, based on its id
     * @throws Exception a server-side error occurred
     */
    private Path constructJobDirectory() {
        String id = getWesRun().getId();
        return Paths.get(
            getEngineConfig().getRundir(),
            id.substring(0, 2),
            id.substring(2, 4),
            id.substring(4, 6),
            id
        );
    }

    /* Setters and Getters */

    @Override
    public void setEngineConfig(EngineConfig engineConfig) {
        this.engineConfig = (NativeEngineConfig) engineConfig;
    }

    @Override
    public NativeEngineConfig getEngineConfig() {
        return engineConfig;
    }

    public void setJobDirectory(Path jobDirectory) {
        this.jobDirectory = jobDirectory;
    }

    public Path getJobDirectory() {
        return jobDirectory;
    }
}
