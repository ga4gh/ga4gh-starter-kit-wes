package org.ga4gh.starterkit.wes.config.language;

import java.util.ArrayList;

import org.ga4gh.starterkit.wes.config.engine.NativeEngineConfig;
import org.ga4gh.starterkit.wes.model.WorkflowType;

public class NextflowLanguageConfig extends AbstractLanguageConfig {

    // private final WorkflowType type = WorkflowType.NEXTFLOW;
    private WorkflowType type;

    public NextflowLanguageConfig() {
        setType(WorkflowType.NEXTFLOW);
        setEnabled(true);
        setVersions(new ArrayList<>() {{
            add("21.04.0");
        }});
        setEngineConfig(new NativeEngineConfig());
    }

    public void setType(WorkflowType type) {
        this.type = type;
    }

    @Override
    public WorkflowType getType() {
        return type;
    }
}
