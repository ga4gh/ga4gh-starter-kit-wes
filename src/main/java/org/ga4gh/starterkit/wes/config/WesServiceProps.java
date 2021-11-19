package org.ga4gh.starterkit.wes.config;

import org.ga4gh.starterkit.wes.config.language.NextflowLanguageConfig;

public class WesServiceProps {

    private NextflowLanguageConfig nextflow;

    public WesServiceProps() {
        nextflow = new NextflowLanguageConfig();
    }

    public void setNextflow(NextflowLanguageConfig nextflow) {
        this.nextflow = nextflow;
    }

    public NextflowLanguageConfig getNextflow() {
        return nextflow;
    }
}
