package org.ga4gh.starterkit.wes.utils.runmanager.detailshandler.type;

import java.net.URL;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.ga4gh.starterkit.wes.model.RunStatus;
import org.ga4gh.starterkit.wes.model.State;
import org.ga4gh.starterkit.wes.model.WesRun;

public class NextflowTypeDetailsHandler extends AbstractRunTypeDetailsHandler {

    private WesRun wesRun;

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
            workflowSignature
        };
    }

    // for reading workflow run state

    public Map<String, String> requestFileContentsToDetermineRunStatus() throws Exception {
        List<String> workDirContents = getRunEngineDetailsHandler().provideDirectoryContents("work");
        String workSubdir = workDirContents.get(0);
        List<String> workSubdirContents = getRunEngineDetailsHandler().provideDirectoryContents(Paths.get("work", workSubdir).toString());
        String workSubdirSubdir = workSubdirContents.get(0);

        Map<String,String> requestedFiles = new HashMap<>() {{
            put("exitcode", Paths.get("work", workSubdir, workSubdirSubdir, ".exitcode").toString());
        }};

        return requestedFiles;
    }

    public RunStatus determineRunStatus(Map<String, String> requestedFileContentsMap) {
        State state = State.UNKNOWN;
        RunStatus runStatus = new RunStatus(getWesRun().getId(), state);

        switch (requestedFileContentsMap.get("exitcode")) {
            case "0":
                runStatus.setState(State.COMPLETE);
                break;
            default:
                runStatus.setState(State.EXECUTOR_ERROR);
        }
        return runStatus;
    }

    // private convenience methods

    private String getWorkflowSignature() throws Exception {
        URL url = new URL(wesRun.getWorkflowUrl());
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

    /* Setters and getters */

    public void setWesRun(WesRun wesRun) {
        this.wesRun = wesRun;
    }

    public WesRun getWesRun() {
        return wesRun;
    }
    
}
