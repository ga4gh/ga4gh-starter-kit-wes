package org.ga4gh.starterkit.wes.controller;

import static org.ga4gh.starterkit.wes.constant.WesApiConstants.WES_API_V1;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import org.ga4gh.starterkit.wes.model.RunId;
import org.ga4gh.starterkit.wes.model.RunLog;
import org.ga4gh.starterkit.wes.model.RunStatus;
import org.ga4gh.starterkit.wes.model.State;
import org.ga4gh.starterkit.wes.model.WorkflowType;
import org.ga4gh.starterkit.wes.utils.requesthandler.GetRunLogRequestHandler;
import org.ga4gh.starterkit.wes.utils.requesthandler.GetRunStatusRequestHandler;
import org.ga4gh.starterkit.wes.utils.requesthandler.SubmitRunRequestHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(WES_API_V1 + "/runs")
public class Runs {

    @Resource
    private SubmitRunRequestHandler submitRunRequest;

    @Resource
    private GetRunLogRequestHandler getRunLog;

    @Resource
    private GetRunStatusRequestHandler getRunStatus;

    @GetMapping
    public List<RunStatus> getRuns() {
        List<RunStatus> runs = new ArrayList<>() {{
            add(new RunStatus("80cf1f59-c7de-445a-a431-34f85734eef3", State.COMPLETE));
        }};
        return runs;
    }

    @PostMapping
    public RunId createRun(
        @RequestParam("workflow_type") WorkflowType workflowType,
        @RequestParam("workflow_type_version") String workflowTypeVersion,
        @RequestParam("workflow_url") String workflowUrl,
        @RequestParam("workflow_params") String workflowParams,
        @RequestParam(name = "tags", required = false) String tags,
        @RequestParam(name = "workflow_engine_parameters", required = false) String workflowEngineParameters
        // @RequestParam("workflow_attachment") List<String> workflowAttachment
    ) {
        return submitRunRequest.prepare(workflowType, workflowTypeVersion, workflowUrl, workflowParams, tags, null).handleRequest();
    }

    @GetMapping(path = "/{run_id:.+}")
    public RunLog getRunLog(
        @PathVariable(name = "run_id") String runId
    ) {
        return getRunLog.prepare(runId).handleRequest();
    }

    @PostMapping(path = "/{run_id:.+}/cancel")
    public String cancelRun(
        @PathVariable(name = "run_id") String runId
    ) {
        return runId;
    }

    @GetMapping(path = "/{run_id:.+}/status")
    public RunStatus runStatus(
        @PathVariable(name = "run_id") String runId
    ) {
        return getRunStatus.prepare(runId).handleRequest();
    }
}