package org.ga4gh.starterkit.wes.utils.runmanager.engine;

import org.ga4gh.starterkit.wes.config.engine.EngineConfig;
import org.ga4gh.starterkit.wes.model.WesRun;
import org.ga4gh.starterkit.wes.utils.runmanager.language.LanguageHandler;

/**
 * Abstract class containing default implementations for simple setters, getters, etc.
 */
public abstract class AbstractEngineHandler implements EngineHandler {

    private WesRun wesRun;
    private EngineConfig engineConfig;
    private LanguageHandler languageHandler;
    private Class<?> exceptionClass;
    private String exceptionMessage;

    public void setWesRun(WesRun wesRun) {
        this.wesRun = wesRun;
    }

    public WesRun getWesRun() {
        return wesRun;
    }

    public void setEngineConfig(EngineConfig engineConfig) {
        this.engineConfig = engineConfig;
    }

    public EngineConfig getEngineConfig() {
        return engineConfig;
    }

    public void setLanguageHandler(LanguageHandler languageHandler) {
        this.languageHandler = languageHandler;
    }

    public LanguageHandler getLanguageHandler() {
        return languageHandler;
    }

    public void setExceptionClass(Class<?> exceptionClass) {
        this.exceptionClass = exceptionClass;
    }

    public Class<?> getExceptionClass() {
        return exceptionClass;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public boolean errored() {
        boolean errored = true;
        if (getExceptionClass() == null) {
            errored = false;
        }
        return errored;
    }

    public void setException(Exception ex) {
        setExceptionClass(ex.getClass());
        setExceptionMessage(ex.getMessage());
    }
}
