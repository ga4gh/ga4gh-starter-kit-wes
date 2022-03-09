package org.ga4gh.starterkit.wes.utils.runmanager.engine;

import java.io.IOException;
import java.util.List;
import org.ga4gh.starterkit.wes.config.engine.FilesystemEngineConfig;
import org.ga4gh.starterkit.wes.utils.runmanager.language.CommandOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Facilitates access to data/information for workflow runs launched via the 
 * 'Native' engine, that is, launched on the host machine without leveraging any
 * HPC or Cloud batch scheduling resources
 */
@Setter
@Getter
@NoArgsConstructor
public class FileSystemEngineHandler extends AbstractEngineHandler {

    private FileSystemEngineOperator filesystemEngineOperator;

    @Override
    public void setup() {
        try {
            FileSystemEngineOperator operator = new FileSystemEngineOperator();
            operator.setWesRun(getWesRun());
            operator.setEngineConfig((FilesystemEngineConfig) getEngineConfig());
            operator.setup();
            setFilesystemEngineOperator(operator);
        } catch (IOException ex) {
            setException(ex);
        }
    }

    @Override
    public List<String> provideDirectoryContents(String directory) {
        return getFilesystemEngineOperator().provideDirectoryContents(directory);
    }

    @Override
    public FileMetadata provideFileAttributes(String filename) {
        try {
            return getFilesystemEngineOperator().provideFileAttributes(filename);
        } catch (IOException ex) {
            setException(ex);
            return null;
        }
    }

    @Override
    public String getRequestedFileContents(String filename) {
        try {
            return getFilesystemEngineOperator().getRequestedFileContents(filename);
        } catch (IOException ex) {
            setException(ex);
            return null;
        }
    }

    @Override
    public CommandOutput getRequestedCommandOutput(String[] command) {
        try {
            return getFilesystemEngineOperator().getRequestedCommandOutput(command);
        } catch (IOException | InterruptedException ex) {
            setException(ex);
            return null;
        }
    }

    @Override
    public void writeContentToFile(String filePath, String content) {
        try {
            getFilesystemEngineOperator().writeContentToFile(filePath, content);
        } catch (IOException ex) {
            setException(ex);
        }
    }

    @Override
    public void stageWorkingArea() {
        try {
            getFilesystemEngineOperator().stageWorkingArea();
        } catch (IOException ex) {
            setException(ex);
        }
    }

    @Override
    public void launchWorkflowRunCommand(String[] workflowRunCommand) {
        try {
            getFilesystemEngineOperator().launchWorkflowRunCommand(workflowRunCommand);
        } catch (IOException ex) {
            setException(ex);
        }
    }
}
