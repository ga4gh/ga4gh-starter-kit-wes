package org.ga4gh.starterkit.wes.utils.requesthandler.logs;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.ga4gh.starterkit.common.exception.ResourceNotFoundException;
import org.ga4gh.starterkit.common.requesthandler.RequestHandler;
import org.ga4gh.starterkit.wes.model.WesRun;
import org.ga4gh.starterkit.wes.utils.hibernate.WesHibernateUtil;
import org.ga4gh.starterkit.wes.utils.runmanager.RunManager;
import org.ga4gh.starterkit.wes.utils.runmanager.RunManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Request handling logic for getting task-level logs from a single task of a
 * nextflow workflow run
 */
public class NextflowTaskLogsRequestHandler implements RequestHandler<String> {

    @Autowired
    private RunManagerFactory runManagerFactory;

    @Autowired
    private WesHibernateUtil hibernateUtil;

    private String channel;
    private String runId;
    private String subdirA;
    private String subdirB;

    /**
     * Prepares the request handler with input params from the controller function
     * @param channel 'stdout' or 'stderr' 
     * @param runId run identifier
     * @param subdirA the first subdirectory under the 'work' directory, indicating working directory for the task
     * @param subdirB the second subdirectory under the 'work' directory, indicating working directory for the task
     * @return the prepared request handler
     */
    public NextflowTaskLogsRequestHandler prepare(String channel, String runId,
        String subdirA, String subdirB
    ) {
        this.channel = channel;
        this.runId = runId;
        this.subdirA = subdirA;
        this.subdirB = subdirB;
        return this;
    }

    /**
     * Obtains raw stdout/stderr log output for the requested task
     */
    public String handleRequest() {
        WesRun wesRun = hibernateUtil.readEntityObject(WesRun.class, runId, true);
        if (wesRun == null) {
            throw new ResourceNotFoundException("No WES Run by the id: " + runId);
        }
        RunManager runManager = runManagerFactory.createRunManager(wesRun);

        // construct task log file path based on the task subdirs. Stdout/stderr
        // files are located under the 'work' directory 
        String channelFileSuffix = channel.equals("stdout") ? "out" : "err";
        Path filePath = Paths.get("work", subdirA, subdirB, ".command." + channelFileSuffix);
        
        try {
            return runManager.getEngineHandler().getRequestedFileContents(filePath.toString());
        } catch (Exception ex) {
            return null;
        }
    }
}
