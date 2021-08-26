package org.ga4gh.starterkit.wes.app;

import org.ga4gh.starterkit.common.config.ContainsServerProps;
import org.ga4gh.starterkit.common.config.ServerProps;

/**
 * Top-level configuration container object for standalone deployments. To
 * be deserialized/loaded as part of a YAML config file specified on the
 * command line.
 */
public class WesServerYamlConfigContainer implements ContainsServerProps {

    /**
     * Nested configuration object
     */
    private WesServerYamlConfig wes;

    /**
     * Instantiates a new WesStandaloneYamlConfigContainer object with default properties
     */
    public WesServerYamlConfigContainer() {
        wes = new WesServerYamlConfig();
    }

    /**
     * Instantiates a new WesStandaloneYamlConfigContainer while assigning an existing WesStandaloneYamlConfig object
     * @param wes WesStandaloneYamlConfig object
     */
    public WesServerYamlConfigContainer(WesServerYamlConfig wes) {
        this.wes = wes;
    }

    public ServerProps getServerProps() {
        return getWes().getServerProps();
    }

    /**
     * Assign wes
     * @param wes WesStandaloneYamlConfig object
     */
    public void setWes(WesServerYamlConfig wes) {
        this.wes = wes;
    }

    /**
     * Retrieve wes
     * @return WesStandaloneYamlConfig object
     */
    public WesServerYamlConfig getWes() {
        return wes;
    }
}
