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

@Configuration
@ConfigurationProperties
public class WesStandaloneSpringConfig implements WebMvcConfigurer {

    /* ******************************
     * WES SERVER CONFIG BEANS
     * ****************************** */

    @Bean
    public Options getCommandLineOptions() {
        final Options options = new Options();
        options.addOption("c", "config", true, "Path to WES YAML config file");
        return options;
    }

    @Bean
    @Scope("prototype")
    @Qualifier(WesStandaloneConstants.EMPTY_WES_CONFIG_CONTAINER)
    public WesStandaloneYamlConfigContainer emptyWesConfigContainer() {
        return new WesStandaloneYamlConfigContainer(new WesStandaloneYamlConfig());
    }

    @Bean
    @Qualifier(WesStandaloneConstants.DEFAULT_WES_CONFIG_CONTAINER)
    public WesStandaloneYamlConfigContainer defaultWesConfigContainer() {
        return new WesStandaloneYamlConfigContainer(new WesStandaloneYamlConfig());
    }

    @Bean
    @Qualifier(WesStandaloneConstants.USER_WES_CONFIG_CONTAINER)
    public WesStandaloneYamlConfigContainer runtimeWesConfigContainer(
        @Autowired ApplicationArguments args,
        @Autowired() Options options,
        @Qualifier(WesStandaloneConstants.EMPTY_WES_CONFIG_CONTAINER) WesStandaloneYamlConfigContainer wesConfigContainer
    ) {
        WesStandaloneYamlConfigContainer userConfigContainer = CliYamlConfigLoader.load(WesStandaloneYamlConfigContainer.class, args, options, "config");
        if (userConfigContainer != null) {
            return userConfigContainer;
        }
        return wesConfigContainer;
    }

    @Bean
    @Qualifier(WesStandaloneConstants.FINAL_WES_CONFIG_CONTAINER)
    public WesStandaloneYamlConfigContainer mergedWesConfigContainer(
        @Qualifier(WesStandaloneConstants.DEFAULT_WES_CONFIG_CONTAINER) WesStandaloneYamlConfigContainer defaultContainer,
        @Qualifier(WesStandaloneConstants.USER_WES_CONFIG_CONTAINER) WesStandaloneYamlConfigContainer userContainer
    ) {
        DeepObjectMerger.merge(userContainer, defaultContainer);
        return defaultContainer;
    }

    @Bean
    public ServerProps getServerProps(
        @Qualifier(WesStandaloneConstants.FINAL_WES_CONFIG_CONTAINER) WesStandaloneYamlConfigContainer wesConfigContainer
    ) {
        return wesConfigContainer.getWes().getServerProps();
    }

    @Bean
    public DatabaseProps getDatabaseProps(
        @Qualifier(WesStandaloneConstants.FINAL_WES_CONFIG_CONTAINER) WesStandaloneYamlConfigContainer wesConfigContainer
    ) {
        return wesConfigContainer.getWes().getDatabaseProps();
    }

    @Bean
    public WesServiceInfo getServiceInfo(
        @Qualifier(WesStandaloneConstants.FINAL_WES_CONFIG_CONTAINER) WesStandaloneYamlConfigContainer wesConfigContainer
    ) {
        return wesConfigContainer.getWes().getServiceInfo();
    }
}
