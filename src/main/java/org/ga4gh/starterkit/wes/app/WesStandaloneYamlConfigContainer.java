package org.ga4gh.starterkit.wes.app;

/**
 * Top-level configuration container object for standalone deployments. To
 * be deserialized/loaded as part of a YAML config file specified on the
 * command line.
 */
public class WesStandaloneYamlConfigContainer {

    /**
     * Nested configuration object
     */
    private WesStandaloneYamlConfig wes;

    /**
     * Instantiates a new WesStandaloneYamlConfigContainer object with default properties
     */
    public WesStandaloneYamlConfigContainer() {
        wes = new WesStandaloneYamlConfig();
    }

    /**
     * Instantiates a new WesStandaloneYamlConfigContainer while assigning an existing WesStandaloneYamlConfig object
     * @param wes
     */
    public WesStandaloneYamlConfigContainer(WesStandaloneYamlConfig wes) {
        this.wes = wes;
    }

    /**
     * Assign wes
     * @param wes WesStandaloneYamlConfig object
     */
    public void setWes(WesStandaloneYamlConfig wes) {
        this.wes = wes;
    }

    /**
     * Retrieve wes
     * @return WesStandaloneYamlConfig object
     */
    public WesStandaloneYamlConfig getWes() {
        return wes;
    }
}
