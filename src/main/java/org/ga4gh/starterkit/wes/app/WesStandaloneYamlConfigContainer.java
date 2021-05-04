package org.ga4gh.starterkit.wes.app;

public class WesStandaloneYamlConfigContainer {

    private WesStandaloneYamlConfig wes;

    public WesStandaloneYamlConfigContainer() {
        wes = new WesStandaloneYamlConfig();
    }

    public WesStandaloneYamlConfigContainer(WesStandaloneYamlConfig wes) {
        this.wes = wes;
    }

    public void setWes(WesStandaloneYamlConfig wes) {
        this.wes = wes;
    }

    public WesStandaloneYamlConfig getWes() {
        return wes;
    }
}
