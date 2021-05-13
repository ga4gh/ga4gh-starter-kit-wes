package org.ga4gh.starterkit.wes.utils.runmanager.detailshandler.type;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.ga4gh.starterkit.wes.model.RunLog;
import org.ga4gh.starterkit.wes.model.RunStatus;
import org.ga4gh.starterkit.wes.model.State;
import org.ga4gh.starterkit.wes.model.WesLog;

public class NextflowTypeDetailsHandler extends AbstractRunTypeDetailsHandler {

    public NextflowTypeDetailsHandler() {

    }

    // for submission of workflows

    public String[] constructWorkflowRunCommand() throws Exception {
        String workflowSignature = getWorkflowSignature();
        return new String[] {
            "nextflow",
            "run",
            "-r",
            "main",
            "-params-file",
            "params.json",
            "-name",
            constructNextflowRunName(),
            workflowSignature
        };
    }

    // for reading workflow run state

    public RunStatus determineRunStatus() throws Exception {
        RunStatus runStatus = new RunStatus(getWesRun().getId(), State.UNKNOWN);
        String runLogStdout = requestCommandStdoutFromEngine(new String[] {"nextflow", "log"});
        String[] runLog = runLogStdout.split("\n")[1].split("\t");
        String runLogStatus = runLog[3].strip();
        if (runLogStatus.equals("OK")) {
            runStatus.setState(State.COMPLETE);
            return runStatus;
        }
        return runStatus;
    }

    // for reading workflow log

    public WesLog determineRunLog() {
        return null;
    }

    public List<WesLog> determineTaskLogs() {
        return null;
    }

    public Map<String, String> determineOutputs() {
        return null;
    }

    // private convenience methods

    private String getWorkflowSignature() throws Exception {
        URL url = new URL(getWesRun().getWorkflowUrl());
        String urlHost = url.getHost();
        String workflowSignature = null;
        switch (urlHost) {
            case "github.com":
                workflowSignature = githubWorkflowSignature(url);
                break;
        }
        return workflowSignature;
    }

    private String githubWorkflowSignature(URL url) {
        String urlPath = url.getPath();
        List<String> urlPathSplit = Arrays.asList(urlPath.split("/"));
        return String.join("/", urlPathSplit.subList(1, 3));
    }

    private String constructNextflowRunName() {
        return "job" + getWesRun().getId().substring(0, 5);
    }
}
