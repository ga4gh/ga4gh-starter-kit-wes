package org.ga4gh.starterkit.wes.utils.runmanager.language;

import org.ga4gh.starterkit.wes.model.WesRun;
import org.ga4gh.starterkit.wes.utils.runmanager.engine.EngineHandler;

/**
 * Abstract class containing default implementations for simple setters, getters, etc.
 */
public abstract class AbstractLanguageHandler implements LanguageHandler {

    private WesRun wesRun;
    private EngineHandler runEngineDetailsHandler;

    public void setWesRun(WesRun wesRun) {
        this.wesRun = wesRun;
    }

    public WesRun getWesRun() {
        return wesRun;
    }

    public void setRunEngineDetailsHandler(EngineHandler runEngineDetailsHandler)  {
        this.runEngineDetailsHandler = runEngineDetailsHandler;
    }

    public EngineHandler getRunEngineDetailsHandler() {
        return runEngineDetailsHandler;
    }

    public String requestFileContentsFromEngine(String filename) throws Exception {
        return getRunEngineDetailsHandler().getRequestedFileContents(filename);
    }

    public String requestCommandStdoutFromEngine(String[] command) throws Exception {
        return getRunEngineDetailsHandler().getRequestedCommandStdout(command);
    }
}
