package org.ga4gh.starterkit.wes.utils.requesthandler.logs;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.util.Strings;
import org.ga4gh.starterkit.common.exception.ResourceNotFoundException;
import org.ga4gh.starterkit.common.requesthandler.RequestHandler;
import org.ga4gh.starterkit.wes.model.WesRun;
import org.ga4gh.starterkit.wes.utils.hibernate.WesHibernateUtil;
import org.ga4gh.starterkit.wes.utils.runmanager.RunManager;
import org.ga4gh.starterkit.wes.utils.runmanager.RunManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Request handling logic for getting run-level logs from all tasks of a
 * nextflow workflow run 
 */
public class NextflowWorkflowLogsRequestHandler implements RequestHandler<String> {

    @Autowired
    private RunManagerFactory runManagerFactory;

    @Autowired
    private WesHibernateUtil hibernateUtil;

    private String channel;
    private String runId;
    private String workdirs;

    /**
     * Prepares the request handler with input params from the controller function
     * @param channel 'stdout' or 'stderr'
     * @param runId run identifier
     * @param workdirs comma-delimited list of all task-level working directories
     * @return the prepared request handler
     */
    public NextflowWorkflowLogsRequestHandler prepare(String channel, String runId, String workdirs) {
        this.channel = channel;
        this.runId = runId;
        this.workdirs = workdirs;
        return this;
    }

    /**
     * Obtains a raw stdout/stderr log output for the request workflow run
     */
    public String handleRequest() {
        List<String> workflowLogs = new ArrayList<>();

        WesRun wesRun = hibernateUtil.readEntityObject(WesRun.class, runId, true);
        if (wesRun == null) {
            throw new ResourceNotFoundException("No WES Run by the id: " + runId);
        }
        RunManager runManager = runManagerFactory.createRunManager(wesRun);
        String channelFileSuffix = channel.equals("stdout") ? "out" : "err";

        // for each workdir (task) in the comma delimited list, load the stdout
        // or stderr output. the final result is the concatenated output of all
        // tasks
        String[] workdirsSplit = workdirs.split(",");
        try {
            for (String workdir : workdirsSplit) {
                Path filePath = Paths.get("work", workdir, ".command." + channelFileSuffix);
                String logfileContents = runManager.getEngineHandler().getRequestedFileContents(filePath.toString());
                workflowLogs.add(logfileContents);
            }
        } catch (Exception ex) {
            return null;
        }
        
        return Strings.join(workflowLogs, '\n');
    }
}
