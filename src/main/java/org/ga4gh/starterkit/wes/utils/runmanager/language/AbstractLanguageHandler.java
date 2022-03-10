package org.ga4gh.starterkit.wes.utils.runmanager.language;

import org.ga4gh.starterkit.wes.config.language.LanguageConfig;
import org.ga4gh.starterkit.wes.model.WesRun;
import org.ga4gh.starterkit.wes.utils.runmanager.engine.EngineHandler;
import lombok.Getter;
import lombok.Setter;

/**
 * Abstract language handler containing default implementations of setters, getters, etc.
 */
@Setter
@Getter
public abstract class AbstractLanguageHandler implements LanguageHandler {

    private WesRun wesRun;
    private LanguageConfig languageConfig;
    private EngineHandler engineHandler;

    public String requestFileContentsFromEngine(String filename) throws Exception {
        return getEngineHandler().getRequestedFileContents(filename);
    }

    public CommandOutput requestCommandOutputFromEngine(String[] command) throws Exception {
        return getEngineHandler().getRequestedCommandOutput(command);
    }
}
