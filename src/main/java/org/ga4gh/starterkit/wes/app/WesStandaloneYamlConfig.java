package org.ga4gh.starterkit.wes.app;

import org.ga4gh.starterkit.common.config.DatabaseProps;
import org.ga4gh.starterkit.common.config.ServerProps;
import org.ga4gh.starterkit.wes.model.WesServiceInfo;

public class WesStandaloneYamlConfig {

    private ServerProps serverProps;
    private DatabaseProps databaseProps;
    private WesServiceInfo serviceInfo;

    public WesStandaloneYamlConfig() {
        serverProps = new ServerProps();
        databaseProps = new DatabaseProps();
        serviceInfo = new WesServiceInfo();
    }

    public void setServerProps(ServerProps serverProps) {
        this.serverProps = serverProps;
    }

    public ServerProps getServerProps() {
        return serverProps;
    }

    public void setDatabaseProps(DatabaseProps databaseProps) {
        this.databaseProps = databaseProps;
    }

    public DatabaseProps getDatabaseProps() {
        return databaseProps;
    }

    public void setServiceInfo(WesServiceInfo serviceInfo) {
        this.serviceInfo = serviceInfo;
    }

    public WesServiceInfo getServiceInfo() {
        return serviceInfo;
    }
}
