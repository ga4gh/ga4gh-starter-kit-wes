package org.ga4gh.starterkit.wes.config;

import org.ga4gh.starterkit.wes.config.language.LanguageConfig;
import org.ga4gh.starterkit.wes.config.language.NextflowLanguageConfig;
import org.ga4gh.starterkit.wes.model.WorkflowType;

public class WesServiceProps {

    private NextflowLanguageConfig nextflow;

    public WesServiceProps() {
        nextflow = new NextflowLanguageConfig();
    }

    public LanguageConfig getLanguageConfig(WorkflowType workflowType) {
        LanguageConfig languageConfig = null;
        switch (workflowType) {
            case NEXTFLOW:
                languageConfig = getNextflow();
                break;
            default:
                languageConfig = null;
                break;
        }
        return languageConfig;
    }

    public void setNextflow(NextflowLanguageConfig nextflow) {
        this.nextflow = nextflow;
    }

    public NextflowLanguageConfig getNextflow() {
        return nextflow;
    }
}
