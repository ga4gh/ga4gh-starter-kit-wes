package org.ga4gh.starterkit.wes.utils.runmanager.language;

import org.ga4gh.starterkit.wes.config.language.LanguageConfig;
import org.ga4gh.starterkit.wes.model.WesRun;
import org.ga4gh.starterkit.wes.utils.runmanager.engine.EngineHandler;

/**
 * Abstract class containing default implementations for simple setters, getters, etc.
 */
public abstract class AbstractLanguageHandler implements LanguageHandler {

    private WesRun wesRun;
    private LanguageConfig languageConfig;
    private EngineHandler engineHandler;

    public void setWesRun(WesRun wesRun) {
        this.wesRun = wesRun;
    }

    public WesRun getWesRun() {
        return wesRun;
    }

    public void setLanguageConfig(LanguageConfig languageConfig) {
        this.languageConfig = languageConfig;
    }

    public LanguageConfig getLanguageConfig() {
        return languageConfig;
    }

    public void setEngineHandler(EngineHandler engineHandler)  {
        this.engineHandler = engineHandler;
    }

    public EngineHandler getEngineHandler() {
        return engineHandler;
    }

    public String requestFileContentsFromEngine(String filename) throws Exception {
        return getEngineHandler().getRequestedFileContents(filename);
    }

    public CommandOutput requestCommandOutputFromEngine(String[] command) throws Exception {
        return getEngineHandler().getRequestedCommandOutput(command);
    }
}
