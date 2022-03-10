package org.ga4gh.starterkit.wes.utils.runmanager.engine;

import java.util.List;
import org.ga4gh.starterkit.wes.config.engine.EngineConfig;
import org.ga4gh.starterkit.wes.model.WesRun;
import org.ga4gh.starterkit.wes.utils.runmanager.language.CommandOutput;
import org.ga4gh.starterkit.wes.utils.runmanager.language.LanguageHandler;

/**
 * Generic interface for classes facilitating data access under a particular
 * workflow engine/job scheduling system
 */
public interface EngineHandler {

    // common operations
    public void setup();
    public void setWesRun(WesRun wesRun);
    public WesRun getWesRun();
    public void setEngineConfig(EngineConfig engineConfig);
    public EngineConfig getEngineConfig();
    public void setLanguageHandler(LanguageHandler languageHandler);
    public LanguageHandler getLanguageHandler();

    // capture exception state
    public void setExceptionClass(Class<?> exceptionClass);
    public Class<?> getExceptionClass();
    public void setExceptionMessage(String exceptionMessage);
    public String getExceptionMessage();
    public boolean errored();

    /**
     * List the contents (files, subdirectories) of the requested directory
     * @param directory requested directory
     * @return contents of directory
     * @throws Exception a server-side error occurred
     */
    public List<String> provideDirectoryContents(String directory);

    /**
     * Read and return the contents of a requested file
     * @param filename requested file name/path
     * @return contents of file
     * @throws Exception a server-side error occurred
     */
    public String getRequestedFileContents(String filename);

    public FileMetadata provideFileAttributes(String filename);

    /**
     * Get the stdout of a requested CLI command, when run in the run's working directory
     * @param command requested CLI command
     * @return stdout of command
     * @throws Exception a server-side error occurred
     */
    public CommandOutput getRequestedCommandOutput(String[] command);

    // for launching workflow runs

    public void writeContentToFile(String filePath, String content);

    /**
     * Prepare the working directory/area according to what is needed by the engine
     * @throws Exception a server-side error occurred
     */
    public void stageWorkingArea();

    /**
     * Execute the provided workflow run command through the engine
     * @param workflowRunCommand command to execute that will initiate the workflow run
     * @throws Exception a server-side error occurred
     */
    public void launchWorkflowRunCommand(String[] workflowRunCommand);
}
