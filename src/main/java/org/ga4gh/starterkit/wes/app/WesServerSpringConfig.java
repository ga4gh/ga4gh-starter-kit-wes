package org.ga4gh.starterkit.wes.app;

import org.apache.commons.cli.Options;
import org.ga4gh.starterkit.common.config.DatabaseProps;
import org.ga4gh.starterkit.common.config.ServerProps;
import org.ga4gh.starterkit.common.util.CliYamlConfigLoader;
import org.ga4gh.starterkit.common.util.DeepObjectMerger;
import org.ga4gh.starterkit.wes.model.WesServiceInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Contains Spring bean definitions that are to be loaded only during WES
 * standalone deployments (ie. not when being run as a GA4GH multi-API service)
 * 
 * @see org.ga4gh.starterkit.wes.beanconfig.StarterKitWesSpringConfig Spring config common to standalone and non-standalone deployments
 */
@Configuration
@ConfigurationProperties
public class WesServerSpringConfig implements WebMvcConfigurer {

    /* ******************************
     * WES SERVER CONFIG BEANS
     * ****************************** */

    /**
     * Loads command line options object, to enable parsing of program args
     * @return valid command line options to be parsed
     */
    @Bean
    public Options getCommandLineOptions() {
        final Options options = new Options();
        options.addOption("c", "config", true, "Path to WES YAML config file");
        return options;
    }

    /**
     * Loads an empty WES config container
     * @return WES config container with empty properties
     */
    @Bean
    @Scope("prototype")
    @Qualifier(WesServerConstants.EMPTY_WES_CONFIG_CONTAINER)
    public WesServerYamlConfigContainer emptyWesConfigContainer() {
        return new WesServerYamlConfigContainer(new WesServerYamlConfig());
    }

    /**
     * Loads a WES config container singleton containing all default properties
     * @return WES config container containing defaults
     */
    @Bean
    @Qualifier(WesServerConstants.DEFAULT_WES_CONFIG_CONTAINER)
    public WesServerYamlConfigContainer defaultWesConfigContainer() {
        return new WesServerYamlConfigContainer(new WesServerYamlConfig());
    }

    /**
     * Loads a WES config container singleton containing user-specified properties (via config file)
     * @param args command line args
     * @param options valid set of command line options to be parsed
     * @param wesConfigContainer empty WES config container
     * @return WES config container singleton containing user-specified properties
     */
    @Bean
    @Qualifier(WesServerConstants.USER_WES_CONFIG_CONTAINER)
    public WesServerYamlConfigContainer runtimeWesConfigContainer(
        @Autowired ApplicationArguments args,
        @Autowired() Options options,
        @Qualifier(WesServerConstants.EMPTY_WES_CONFIG_CONTAINER) WesServerYamlConfigContainer wesConfigContainer
    ) {
        WesServerYamlConfigContainer userConfigContainer = CliYamlConfigLoader.load(WesServerYamlConfigContainer.class, args, options, "config");
        if (userConfigContainer != null) {
            return userConfigContainer;
        }
        return wesConfigContainer;
    }

    /**
     * Loads the final WES config container singleton containing merged properties between default and user-specified
     * @param defaultContainer contains default properties
     * @param userContainer contains user-specified properties
     * @return contains merged properties
     */
    @Bean
    @Qualifier(WesServerConstants.FINAL_WES_CONFIG_CONTAINER)
    public WesServerYamlConfigContainer mergedWesConfigContainer(
        @Qualifier(WesServerConstants.DEFAULT_WES_CONFIG_CONTAINER) WesServerYamlConfigContainer defaultContainer,
        @Qualifier(WesServerConstants.USER_WES_CONFIG_CONTAINER) WesServerYamlConfigContainer userContainer
    ) {
        DeepObjectMerger.merge(userContainer, defaultContainer);
        return defaultContainer;
    }

    /**
     * Retrieve server props object from merged WES config container
     * @param wesConfigContainer merged WES config container
     * @return merged server props
     */
    @Bean
    public ServerProps getServerProps(
        @Qualifier(WesServerConstants.FINAL_WES_CONFIG_CONTAINER) WesServerYamlConfigContainer wesConfigContainer
    ) {
        return wesConfigContainer.getWes().getServerProps();
    }

    /**
     * Retrieve database props object from merged WES config container
     * @param wesConfigContainer merged WES config container
     * @return merged database props
     */
    @Bean
    public DatabaseProps getDatabaseProps(
        @Qualifier(WesServerConstants.FINAL_WES_CONFIG_CONTAINER) WesServerYamlConfigContainer wesConfigContainer
    ) {
        return wesConfigContainer.getWes().getDatabaseProps();
    }

    /**
     * Retrieve service info object from merged WES config container
     * @param wesConfigContainer merged WES config container
     * @return merged service info object
     */
    @Bean
    public WesServiceInfo getServiceInfo(
        @Qualifier(WesServerConstants.FINAL_WES_CONFIG_CONTAINER) WesServerYamlConfigContainer wesConfigContainer
    ) {
        return wesConfigContainer.getWes().getServiceInfo();
    }
}
