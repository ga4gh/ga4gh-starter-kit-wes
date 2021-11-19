package org.ga4gh.starterkit.wes.utils.runmanager.engine;

import java.util.List;
import org.ga4gh.starterkit.wes.model.WesRun;
import org.ga4gh.starterkit.wes.utils.runmanager.language.LanguageHandler;

/**
 * Generic interface for classes facilitating data access under a particular
 * workflow engine/job scheduling system
 */
public interface EngineHandler {

    // common operations

    /**
     * Assign wesRun
     * @param wesRun entity representing the workflow run
     */
    public void setWesRun(WesRun wesRun);

    /**
     * Retrieve wesRun
     * @return entity representing the workflow run
     */
    public WesRun getWesRun();

    /**
     * Assign the RunTypeDetailsHandler instance belonging to the same run manager as this instance
     * @param runTypeDetailsHandler manages data access for a particular workflow engine
     */
    public void setRunTypeDetailsHandler(LanguageHandler runTypeDetailsHandler);

    /**
     * Retrieve runTypeDetailsHandler
     * @return runTypeDetailsHandler
     */
    public LanguageHandler getRunTypeDetailsHandler();

    /**
     * List the contents (files, subdirectories) of the requested directory
     * @param directory requested directory
     * @return contents of directory
     * @throws Exception a server-side error occurred
     */
    public List<String> provideDirectoryContents(String directory) throws Exception;

    /**
     * Read and return the contents of a requested file
     * @param filename requested file name/path
     * @return contents of file
     * @throws Exception a server-side error occurred
     */
    public String getRequestedFileContents(String filename) throws Exception;

    /**
     * Get the stdout of a requested CLI command, when run in the run's working directory
     * @param command requested CLI command
     * @return stdout of command
     * @throws Exception a server-side error occurred
     */
    public String getRequestedCommandStdout(String[] command) throws Exception;

    // for launching workflow runs

    /**
     * Prepare the working directory/area according to what is needed by the engine
     * @throws Exception a server-side error occurred
     */
    public void stageWorkingArea() throws Exception;

    /**
     * Execute the provided workflow run command through the engine
     * @param workflowRunCommand command to execute that will initiate the workflow run
     * @throws Exception a server-side error occurred
     */
    public void launchWorkflowRunCommand(String[] workflowRunCommand) throws Exception;
}
