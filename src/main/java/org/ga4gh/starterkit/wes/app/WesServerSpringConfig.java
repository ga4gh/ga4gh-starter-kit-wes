package org.ga4gh.starterkit.wes.app;

import org.apache.catalina.connector.Connector;
import org.apache.commons.cli.Options;
import org.ga4gh.starterkit.common.config.DatabaseProps;
import org.ga4gh.starterkit.common.config.ServerProps;
import org.ga4gh.starterkit.common.util.CliYamlConfigLoader;
import org.ga4gh.starterkit.common.util.DeepObjectMerger;
import org.ga4gh.starterkit.common.util.logging.LoggingUtil;
import org.ga4gh.starterkit.common.util.webserver.AdminEndpointsConnector;
import org.ga4gh.starterkit.common.util.webserver.AdminEndpointsFilter;
import org.ga4gh.starterkit.common.util.webserver.CorsFilterBuilder;
import org.ga4gh.starterkit.common.util.webserver.TomcatMultiConnectorServletWebServerFactoryCustomizer;
import org.ga4gh.starterkit.wes.config.WesServiceProps;
import org.ga4gh.starterkit.wes.config.engine.NativeEngineConfig;
import org.ga4gh.starterkit.wes.config.engine.SlurmEngineConfig;
import org.ga4gh.starterkit.wes.config.language.NextflowLanguageConfig;
import org.ga4gh.starterkit.wes.config.language.WdlLanguageConfig;
import org.ga4gh.starterkit.wes.exception.WesCustomExceptionHandling;
import org.ga4gh.starterkit.wes.model.WesServiceInfo;
import org.ga4gh.starterkit.wes.model.WorkflowEngine;
import org.ga4gh.starterkit.wes.model.WorkflowType;
import org.ga4gh.starterkit.wes.utils.hibernate.WesHibernateUtil;
import org.ga4gh.starterkit.wes.utils.requesthandler.GetRunLogRequestHandler;
import org.ga4gh.starterkit.wes.utils.requesthandler.GetRunStatusRequestHandler;
import org.ga4gh.starterkit.wes.utils.requesthandler.SubmitRunRequestHandler;
import org.ga4gh.starterkit.wes.utils.requesthandler.logs.NextflowTaskLogsRequestHandler;
import org.ga4gh.starterkit.wes.utils.requesthandler.logs.NextflowWorkflowLogsRequestHandler;
import org.ga4gh.starterkit.wes.utils.requesthandler.logs.WdlTaskLogsRequestHandler;
import org.ga4gh.starterkit.wes.utils.requesthandler.logs.WdlWorkflowLogsRequestHandler;
import org.ga4gh.starterkit.wes.utils.runmanager.RunManager;
import org.ga4gh.starterkit.wes.utils.runmanager.RunManagerFactory;
import org.ga4gh.starterkit.wes.utils.runmanager.engine.NativeEngineHandler;
import org.ga4gh.starterkit.wes.utils.runmanager.engine.SlurmEngineHandler;
import org.ga4gh.starterkit.wes.utils.runmanager.language.NextflowLanguageHandler;
import org.ga4gh.starterkit.wes.utils.runmanager.language.WdlLanguageHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.filter.CorsFilter;
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
     * TOMCAT SERVER
     * ****************************** */

    @Value("${server.admin.port:4501}")
    private String serverAdminPort;

    @Bean
    public WebServerFactoryCustomizer servletContainer() {
        Connector[] additionalConnectors = AdminEndpointsConnector.additionalConnector(serverAdminPort);
        ServerProperties serverProperties = new ServerProperties();
        return new TomcatMultiConnectorServletWebServerFactoryCustomizer(serverProperties, additionalConnectors);
    }

    @Bean
    public FilterRegistrationBean<AdminEndpointsFilter> adminEndpointsFilter() {
        return new FilterRegistrationBean<AdminEndpointsFilter>(new AdminEndpointsFilter(Integer.valueOf(serverAdminPort)));
    }
    
    @Bean
    public WesCustomExceptionHandling customExceptionHandling() {
        return new WesCustomExceptionHandling();
    }

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter(
        @Autowired ServerProps serverProps
    ) {
        return new CorsFilterBuilder(serverProps).buildFilter();
    }

    /* ******************************
     * YAML CONFIG
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
        DeepObjectMerger merger = new DeepObjectMerger();
        merger.addAtomicClass(WorkflowType.class);
        merger.addAtomicClass(WorkflowEngine.class);
        merger.addAtomicClass(NextflowLanguageConfig.class);
        merger.addAtomicClass(WdlLanguageConfig.class);
        merger.addAtomicClass(NativeEngineConfig.class);
        merger.addAtomicClass(SlurmEngineConfig.class);
        merger.merge(userContainer, defaultContainer);
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

    @Bean
    public WesServiceProps getWesServiceProps(
        @Qualifier(WesServerConstants.FINAL_WES_CONFIG_CONTAINER) WesServerYamlConfigContainer wesConfigContainer
    ) {
        return wesConfigContainer.getWes().getWesServiceProps();
    }

    /**
     * Retrieve service info object from merged WES config container
     * @param wesConfigContainer merged WES config container
     * @return merged service info object
     */
    @Bean
    public WesServiceInfo getServiceInfo(
        @Qualifier(WesServerConstants.FINAL_WES_CONFIG_CONTAINER) WesServerYamlConfigContainer wesConfigContainer,
        @Autowired WesServiceProps wesServiceProps
    ) {
        WesServiceInfo serviceInfo = wesConfigContainer.getWes().getServiceInfo();
        serviceInfo.updateServiceInfoFromWesServiceProps(wesServiceProps);
        return serviceInfo;
    }

    /* ******************************
     * LOGGING
     * ****************************** */

    @Bean
    public LoggingUtil loggingUtil() {
        return new LoggingUtil();
    }

    /* ******************************
     * HIBERNATE CONFIG
     * ****************************** */

    /**
     * Loads/retrieves hibernate util singleton providing access to WES-related database tables
     * @param databaseProps database properties from configuration
     * @return loaded hibernate util singleton
     */
    @Bean
    public WesHibernateUtil wesHibernateUtil(
        @Autowired DatabaseProps databaseProps
    ) {
        WesHibernateUtil hibernateUtil = new WesHibernateUtil();
        hibernateUtil.setDatabaseProps(databaseProps);
        return hibernateUtil;
    }

    /* ******************************
     * REQUEST HANDLER
     * ****************************** */

    /**
     * Get new request handler facilitating the launching of a new workflow run 
     * @return run submission request handler
     */
    @Bean
    @RequestScope
    public SubmitRunRequestHandler submitRunRequestHandler() {
        return new SubmitRunRequestHandler();
    }

    /**
     * Get new request handler facilitating the retrieval of run logs
     * @return run log retrieval request handler
     */
    @Bean
    @RequestScope
    public GetRunLogRequestHandler getRunLogRequestHandler() {
        return new GetRunLogRequestHandler();
    }

    /**
     * Get new request handler facilitating retrieval of run status
     * @return run status retrieval request handler
     */
    @Bean
    @RequestScope
    public GetRunStatusRequestHandler getRunStatusRequestHandler() {
        return new GetRunStatusRequestHandler();
    }

    // NEXTFLOW

    /**
     * Get new request handler providing access to nextflow stdout/stderr logs for a single task
     * @return nextflow task log retrieval request handler
     */
    @Bean
    @RequestScope
    public NextflowTaskLogsRequestHandler nextflowTaskLogsRequestHandler() {
        return new NextflowTaskLogsRequestHandler();
    }

    /**
     * Get new request handler providing access to nextflow stdout/stderr logs for all tasks in a single workflow run
     * @return nextflow workflow run log retrieval request handler
     */
    @Bean
    @RequestScope
    public NextflowWorkflowLogsRequestHandler nextflowWorkflowLogsRequestHandler() {
        return new NextflowWorkflowLogsRequestHandler();
    }

    // WDL

    @Bean
    @RequestScope
    public WdlTaskLogsRequestHandler wdlTaskLogsRequestHandler() {
        return new WdlTaskLogsRequestHandler();
    }

    @Bean
    @RequestScope
    public WdlWorkflowLogsRequestHandler wdlWorkflowLogsRequestHandler() {
        return new WdlWorkflowLogsRequestHandler();
    }

    /* ******************************
     * OTHER UTILS
     * ****************************** */

    /**
     * Loads/retrieves the run manager factory, which spawns low-level run manager objects
     * @return run manager factory singleton
     */
    @Bean
    public RunManagerFactory runManagerFactory() {
        return new RunManagerFactory();
    }

    /**
     * Loads a new run manager instance, which provide low-level access capabilities to workflow run data
     * @return new RunManager instance
     */
    @Bean
    @Scope("prototype")
    public RunManager runManager() {
        return new RunManager();
    }

    /**
     * Loads a new nextflow details handler, which has low-level access to nextflow-type workflow runs
     * @return new NextflowTypeDetailsHandler instance
     */
    @Bean
    @Scope("prototype")
    public NextflowLanguageHandler nextflowLanguageHandler() {
        return new NextflowLanguageHandler();
    }

    @Bean
    @Scope("prototype")
    public WdlLanguageHandler wdlLanguageHandler() {
        return new WdlLanguageHandler();
    }

    /**
     * Loads a new native details handler, which has low-level access to workflow runs submitted via the "Native" engine
     * @return new NativeEngineDetailsHandler instance
     */
    @Bean
    @Scope("prototype")
    public NativeEngineHandler nativeEngineHandler() {
        return new NativeEngineHandler();
    }

    @Bean
    @Scope("prototype")
    public SlurmEngineHandler slurmEngineHandler() {
        return new SlurmEngineHandler();
    }
}
