package org.ga4gh.starterkit.wes.config.language;

import java.util.List;

import org.ga4gh.starterkit.wes.config.engine.EngineConfig;
import org.ga4gh.starterkit.wes.model.WorkflowType;

public interface LanguageConfig {

    public WorkflowType getType();
    public void setEnabled(boolean enabled);
    public boolean getEnabled();
    public void setVersions(List<String> versions);
    public List<String> getVersions();
    public void setEngineConfig(EngineConfig engineConfig);
    public EngineConfig getEngineConfig();
}
