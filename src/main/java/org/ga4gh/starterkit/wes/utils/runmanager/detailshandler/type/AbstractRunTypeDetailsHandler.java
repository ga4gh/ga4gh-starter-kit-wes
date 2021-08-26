package org.ga4gh.starterkit.wes.utils.runmanager.detailshandler.type;

import org.ga4gh.starterkit.wes.model.WesRun;
import org.ga4gh.starterkit.wes.utils.runmanager.detailshandler.engine.RunEngineDetailsHandler;

/**
 * Abstract class containing default implementations for simple setters, getters, etc.
 */
public abstract class AbstractRunTypeDetailsHandler implements RunTypeDetailsHandler {

    private WesRun wesRun;
    private RunEngineDetailsHandler runEngineDetailsHandler;

    public void setWesRun(WesRun wesRun) {
        this.wesRun = wesRun;
    }

    public WesRun getWesRun() {
        return wesRun;
    }

    public void setRunEngineDetailsHandler(RunEngineDetailsHandler runEngineDetailsHandler)  {
        this.runEngineDetailsHandler = runEngineDetailsHandler;
    }

    public RunEngineDetailsHandler getRunEngineDetailsHandler() {
        return runEngineDetailsHandler;
    }

    public String requestFileContentsFromEngine(String filename) throws Exception {
        return getRunEngineDetailsHandler().getRequestedFileContents(filename);
    }

    public String requestCommandStdoutFromEngine(String[] command) throws Exception {
        return getRunEngineDetailsHandler().getRequestedCommandStdout(command);
    }
}
