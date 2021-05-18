package org.ga4gh.starterkit.wes.controller;

import static org.ga4gh.starterkit.wes.constant.WesApiConstants.WES_API_V1;

import javax.annotation.Resource;

import org.ga4gh.starterkit.wes.utils.requesthandler.logs.NextflowTaskLogsRequestHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(WES_API_V1 + "/logs")
public class Logs {

    @Resource
    private NextflowTaskLogsRequestHandler nextflowTaskLogs;

    /* NEXTFLOW LOGS */

    @GetMapping(path = "/nextflow/{channel:.+}/{run_id:.+}/{subdir_a:.+}/{subdir_b:.+}")
    public String getNextflowTaskLogs(
        @PathVariable(name = "channel") String channel,
        @PathVariable(name = "run_id") String runId,
        @PathVariable(name = "subdir_a") String subdirA,
        @PathVariable(name = "subdir_b") String subdirB
    ) {
        return nextflowTaskLogs.prepare(channel, runId, subdirA, subdirB).handleRequest();
    }

    @GetMapping(path = "/nextflow/{channel:.+}/{run_id:.+}")
    public String getNextflowWorkflowLogs(
        @PathVariable(name = "channel") String channel,
        @PathVariable(name = "run_id") String runId
    ) {
        return null;
    }
}
