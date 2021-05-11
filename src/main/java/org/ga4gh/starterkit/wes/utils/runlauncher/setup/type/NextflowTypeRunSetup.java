package org.ga4gh.starterkit.wes.utils.runlauncher.setup.type;

import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.ga4gh.starterkit.wes.model.WesRun;

public class NextflowTypeRunSetup implements WorkflowTypeRunSetup {

    private WesRun wesRun;

    public NextflowTypeRunSetup() {

    }

    public String[] constructCommand() throws Exception {
        String workflowSignature = getWorkflowSignature();
        return new String[] {
            "nextflow",
            "run",
            workflowSignature,
            "-r",
            "main",
            "-params-file",
            "params.json"
        };
    }

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
