package org.ga4gh.starterkit.wes.model;

/**
 * Directly from WES specification, enumeration of all possible states of a 
 * workflow run
 */
public enum State {
    UNKNOWN,
    QUEUED,
    INITIALIZING,
    RUNNING,
    PAUSED,
    COMPLETE,
    EXECUTOR_ERROR,
    SYSTEM_ERROR,
    CANCELED,
    CANCELING
}