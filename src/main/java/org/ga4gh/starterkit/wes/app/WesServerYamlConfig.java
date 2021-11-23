package org.ga4gh.starterkit.wes.app;

import org.ga4gh.starterkit.common.config.DatabaseProps;
import org.ga4gh.starterkit.common.config.ServerProps;
import org.ga4gh.starterkit.wes.config.WesServiceProps;
import org.ga4gh.starterkit.wes.model.WesServiceInfo;

/**
 * Contains multiple configuration objects affecting application behavior.
 * To be deserialized/loaded as part of a YAML config file specified on the
 * command line
 */
public class WesServerYamlConfig {

    /**
     * Server runtime properties
     */
    private ServerProps serverProps;

    /**
     * hibernate API properties determine how to connect to the database
     */
    private DatabaseProps databaseProps;

    /**
     * metadata to be served at /service-info endpoint
     */
    private WesServiceInfo serviceInfo;

    private WesServiceProps wesServiceProps;

    /**
     * Instantiates a new WesStandaloneYamlConfig object with default properties
     */
    public WesServerYamlConfig() {
        serverProps = new ServerProps();
        databaseProps = new DatabaseProps();
        serviceInfo = new WesServiceInfo();
        wesServiceProps = new WesServiceProps();
    }

    /**
     * Assign serverProps
     * @param serverProps ServerProps object
     */
    public void setServerProps(ServerProps serverProps) {
        this.serverProps = serverProps;
    }

    /**
     * Retrieve server props
     * @return server props
     */
    public ServerProps getServerProps() {
        return serverProps;
    }

    /**
     * Assign databaseProps
     * @param databaseProps DatabaseProps object
     */
    public void setDatabaseProps(DatabaseProps databaseProps) {
        this.databaseProps = databaseProps;
    }

    /**
     * Retrieve databaseProps
     * @return databaseProps
     */
    public DatabaseProps getDatabaseProps() {
        return databaseProps;
    }

    /**
     * Assign serviceInfo
     * @param serviceInfo WesServiceInfo object
     */
    public void setServiceInfo(WesServiceInfo serviceInfo) {
        this.serviceInfo = serviceInfo;
    }

    /**
     * Retrieve serviceInfo
     * @return serviceInfo
     */
    public WesServiceInfo getServiceInfo() {
        return serviceInfo;
    }

    public void setWesServiceProps(WesServiceProps wesServiceProps) {
        this.wesServiceProps = wesServiceProps;
    }

    public WesServiceProps getWesServiceProps() {
        return wesServiceProps;
    }
}
