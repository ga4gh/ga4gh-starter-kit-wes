package org.ga4gh.starterkit.wes.controller;

import static org.ga4gh.starterkit.wes.constant.WesApiConstants.WES_API_V1;
import javax.annotation.Resource;
import org.ga4gh.starterkit.wes.utils.requesthandler.logs.NextflowTaskLogsRequestHandler;
import org.ga4gh.starterkit.wes.utils.requesthandler.logs.NextflowWorkflowLogsRequestHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Retrieving low-level stdout and stderr logs for both task and run level logs.
 * Different controller functions are provided for different workflow languages
 */
@RestController
@RequestMapping(WES_API_V1 + "/logs")
public class Logs {

    @Resource
    private NextflowTaskLogsRequestHandler nextflowTaskLogs;

    @Resource
    private NextflowWorkflowLogsRequestHandler nextflowWorkflowLogs;

    /* NEXTFLOW LOGS */

    /**
     * Retrieve task level stdout or stderr logs from a nextflow workflow run
     * @param channel 'stdout' or 'stderr' 
     * @param runId run identifier
     * @param subdirA the first subdirectory under the 'work' directory, indicating working directory for the task
     * @param subdirB the second subdirectory under the 'work' directory, indicating working directory for the task
     * @return raw stdout/stderr log information for a single task
     */
    @GetMapping(path = "/nextflow/{channel:.+}/{run_id:.+}/{subdir_a:.+}/{subdir_b:.+}")
    public String getNextflowTaskLogs(
        @PathVariable(name = "channel") String channel,
        @PathVariable(name = "run_id") String runId,
        @PathVariable(name = "subdir_a") String subdirA,
        @PathVariable(name = "subdir_b") String subdirB
    ) {
        return nextflowTaskLogs.prepare(channel, runId, subdirA, subdirB).handleRequest();
    }

    /**
     * Retrieve workflow run level stdout or stderr logs from a nextflow workflow run
     * @param channel 'stdout' or 'stderr'
     * @param runId run identifier
     * @param workdirs comma-delimited list of all task-level working directories
     * @return raw stdout/stderr logs for all tasks in a workflow run
     */
    @GetMapping(path = "/nextflow/{channel:.+}/{run_id:.+}")
    public String getNextflowWorkflowLogs(
        @PathVariable(name = "channel") String channel,
        @PathVariable(name = "run_id") String runId,
        @RequestParam(name = "workdirs") String workdirs
    ) {
        return nextflowWorkflowLogs.prepare(channel, runId, workdirs).handleRequest();
    }
}
