package org.ga4gh.starterkit.wes.app;

/**
 * String constants for deployment config, generally Spring bean name constants
 * 
 * @since 0.0.3
 * @version 0.0.3
 */
public class WesServerConstants {

    /* Spring bean names - WES config container */

    /**
     * Spring bean qualifier for an empty wes config container
     */
    public static final String EMPTY_WES_CONFIG_CONTAINER = "emptyWesConfigContainer";

    /**
     * Spring bean qualifier for the wes config container containing all defaults
     */
    public static final String DEFAULT_WES_CONFIG_CONTAINER = "defaultWesConfigContainer";

    /**
     * Spring bean qualifier for the wes config container containing user-loaded properties
     */
    public static final String USER_WES_CONFIG_CONTAINER = "userWesConfigContainer";

    /**
     * Spring bean qualifier for the final wes config container containing merge properties
     * from default and user-loaded (user-loaded properties override defaults)
     */
    public static final String FINAL_WES_CONFIG_CONTAINER = "finalWesConfigContainer";

    /* Spring bean scope */

    /**
     * Indicates Spring bean has 'prototype' lifecycle
     */
    public static final String PROTOTYPE = "prototype";
}
