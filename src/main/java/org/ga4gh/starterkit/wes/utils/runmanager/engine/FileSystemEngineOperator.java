package org.ga4gh.starterkit.wes.utils.runmanager.engine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.ga4gh.starterkit.wes.config.engine.FilesystemEngineConfig;
import org.ga4gh.starterkit.wes.model.WesRun;
import org.ga4gh.starterkit.wes.utils.runmanager.language.CommandOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Engine handlers for filesystem-based contexts such as local machine (native)
 * or HPC (Slurm, LSF)
 */
@Setter
@Getter
@NoArgsConstructor
public class FileSystemEngineOperator {

    private WesRun wesRun;
    private FilesystemEngineConfig engineConfig;
    private Path relativeJobDirectory;
    private Path absoluteJobDirectory;

    /* ##################################################
       # COMMON FILE HANDLING OPERATIONS
       ################################################## */

    public void setup() throws IOException {
        String id = getWesRun().getId();
        Path relativeJobDirectory = Paths.get(
            getEngineConfig().getRundir(),
            id.substring(0, 2),
            id.substring(2, 4),
            id.substring(4, 6),
            id
        );
        Path absoluteJobDirectory = Paths.get(relativeJobDirectory.toFile().getCanonicalPath());
        setRelativeJobDirectory(relativeJobDirectory);
        setAbsoluteJobDirectory(absoluteJobDirectory);
    }

    public List<String> provideDirectoryContents(String directory) {
        Path absoluteDirectoryPath = Paths.get(getAbsoluteJobDirectory().toString(), directory);
        return Arrays.asList(absoluteDirectoryPath.toFile().list());
    }

    public FileMetadata provideFileAttributes(String filename) throws IOException {
        FileMetadata fileMetadata = new FileMetadata();
        Path absoluteFilePath = Paths.get(getAbsoluteJobDirectory().toString(), filename);
        fileMetadata.setAbsolutePath(absoluteFilePath.toString());
        fileMetadata.setRelativePath(filename);
        fileMetadata.setFileAttributes(Files.readAttributes(absoluteFilePath, BasicFileAttributes.class));
        return fileMetadata;
    }

    public String getRequestedFileContents(String filename) throws IOException {
        Path absoluteFilePath = Paths.get(getAbsoluteJobDirectory().toString(), filename);
        return new String (Files.readAllBytes(absoluteFilePath));
    }

    public CommandOutput getRequestedCommandOutput(String[] command) throws IOException, InterruptedException {
        CommandOutput commandOutput = new CommandOutput();
        Process process = new ProcessBuilder()
            .command(command)
            .directory(getAbsoluteJobDirectory().toFile())
            .start();
        process.waitFor();
        commandOutput.setExitCode(process.exitValue());
        commandOutput.setStdout(new String (process.getInputStream().readAllBytes()));
        commandOutput.setStderr(new String (process.getErrorStream().readAllBytes()));
        return commandOutput;
    }

    public void writeContentToFile(String filePath, String content) throws IOException {
        Path outputFile = Paths.get(getAbsoluteJobDirectory().toString(), filePath);
        FileUtils.writeStringToFile(outputFile.toFile(), content, "utf-8");
    }

    public void stageWorkingArea() throws IOException {
        Path paramsFile = Paths.get(getAbsoluteJobDirectory().toString(), "params.json");
        Files.createDirectories(getAbsoluteJobDirectory());
        FileUtils.writeStringToFile(paramsFile.toFile(), getWesRun().getWorkflowParams(), "utf-8");
    }

    public void launchWorkflowRunCommand(String[] workflowRunCommand) throws IOException {
        if (workflowRunCommand != null) {
            new ProcessBuilder()
                .command(workflowRunCommand)
                .directory(getAbsoluteJobDirectory().toFile())
                .start();
        }
    }
}
