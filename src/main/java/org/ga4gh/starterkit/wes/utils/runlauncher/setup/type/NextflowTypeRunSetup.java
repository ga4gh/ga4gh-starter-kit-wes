package org.ga4gh.starterkit.wes.utils.runlauncher.setup.type;

public class NextflowTypeRunSetup implements WorkflowTypeRunSetup {

    public NextflowTypeRunSetup() {

    }

    public String[] constructCommand() {
        return new String[] {
            "nextflow",
            "run",
            "jb-adams/md5-nf",
            "-r",
            "main",
            "-params-file",
            "params.json"
        };
    }
    
}
