package org.ga4gh.starterkit.wes.config.language;

import java.util.ArrayList;
import org.ga4gh.starterkit.wes.model.WorkflowEngine;
import org.ga4gh.starterkit.wes.model.WorkflowType;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class WdlLanguageConfig extends AbstractLanguageConfig {

    private String cromwellBaseUrl;

    public WdlLanguageConfig() {
        setEnabled(true);
        setVersions(new ArrayList<>() {{
            add("1.0");
        }});
        setCromwellBaseUrl("http://localhost:8000/");
        setEngine(WorkflowEngine.NATIVE);
    }

    @Override
    public WorkflowType getType() {
        return WorkflowType.WDL;
    }
}
