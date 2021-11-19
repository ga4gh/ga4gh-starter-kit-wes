package org.ga4gh.starterkit.wes.utils.runmanager.engine;

import org.ga4gh.starterkit.wes.model.WesRun;
import org.ga4gh.starterkit.wes.utils.runmanager.language.LanguageHandler;

/**
 * Abstract class containing default implementations for simple setters, getters, etc.
 */
public abstract class AbstractEngineHandler implements EngineHandler {

    private WesRun wesRun;
    private LanguageHandler runTypeDetailsHandler;

    public void setWesRun(WesRun wesRun) {
        this.wesRun = wesRun;
    }

    public WesRun getWesRun() {
        return wesRun;
    }

    public void setRunTypeDetailsHandler(LanguageHandler runTypeDetailsHandler) {
        this.runTypeDetailsHandler = runTypeDetailsHandler;
    }

    public LanguageHandler getRunTypeDetailsHandler() {
        return runTypeDetailsHandler;
    }
}
