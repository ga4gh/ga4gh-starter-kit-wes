package org.ga4gh.starterkit.wes.utils.runmanager.detailshandler.engine;

import org.ga4gh.starterkit.wes.model.WesRun;
import org.ga4gh.starterkit.wes.utils.runmanager.detailshandler.type.RunTypeDetailsHandler;

public abstract class AbstractRunEngineDetailsHandler implements RunEngineDetailsHandler {

    private WesRun wesRun;
    private RunTypeDetailsHandler runTypeDetailsHandler;

    public void setWesRun(WesRun wesRun) {
        this.wesRun = wesRun;
    }

    public WesRun getWesRun() {
        return wesRun;
    }

    public void setRunTypeDetailsHandler(RunTypeDetailsHandler runTypeDetailsHandler) {
        this.runTypeDetailsHandler = runTypeDetailsHandler;
    }

    public RunTypeDetailsHandler getRunTypeDetailsHandler() {
        return runTypeDetailsHandler;
    }
}
