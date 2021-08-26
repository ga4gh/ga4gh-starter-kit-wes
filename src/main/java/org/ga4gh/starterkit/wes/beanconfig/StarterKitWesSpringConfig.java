package org.ga4gh.starterkit.wes.beanconfig;

import org.apache.catalina.connector.Connector;
import org.ga4gh.starterkit.common.config.DatabaseProps;
import org.ga4gh.starterkit.common.util.webserver.AdminEndpointsConnector;
import org.ga4gh.starterkit.common.util.webserver.TomcatMultiConnectorServletWebServerFactoryCustomizer;
import org.ga4gh.starterkit.wes.exception.WesCustomExceptionHandling;
import org.ga4gh.starterkit.wes.utils.hibernate.WesHibernateUtil;
import org.ga4gh.starterkit.wes.utils.requesthandler.GetRunLogRequestHandler;
import org.ga4gh.starterkit.wes.utils.requesthandler.GetRunStatusRequestHandler;
import org.ga4gh.starterkit.wes.utils.requesthandler.SubmitRunRequestHandler;
import org.ga4gh.starterkit.wes.utils.requesthandler.logs.NextflowTaskLogsRequestHandler;
import org.ga4gh.starterkit.wes.utils.requesthandler.logs.NextflowWorkflowLogsRequestHandler;
import org.ga4gh.starterkit.wes.utils.runmanager.RunManager;
import org.ga4gh.starterkit.wes.utils.runmanager.RunManagerFactory;
import org.ga4gh.starterkit.wes.utils.runmanager.detailshandler.engine.NativeEngineDetailsHandler;
import org.ga4gh.starterkit.wes.utils.runmanager.detailshandler.type.NextflowTypeDetailsHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.ga4gh.starterkit.common.util.webserver.AdminEndpointsFilter;
import org.springframework.web.filter.CorsFilter;
import org.ga4gh.starterkit.common.config.ServerProps;
import org.ga4gh.starterkit.common.util.webserver.CorsFilterBuilder;

/**
 * Contains Spring bean definitions that are to be loaded for the WES service under
 * all deployment contexts (i.e. as part of both standalone and GA4GH multi-API service deployments)
 * 
 * @see org.ga4gh.starterkit.wes.app.WesServerSpringConfig Spring config beans used only during standalone deployments
 */
@Configuration
@ConfigurationProperties
public class StarterKitWesSpringConfig {

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
    public NextflowTypeDetailsHandler nextflowTypeDetailsHandler() {
        return new NextflowTypeDetailsHandler();
    }

    /**
     * Loads a new native details handler, which has low-level access to workflow runs submitted via the "Native" engine
     * @return new NativeEngineDetailsHandler instance
     */
    @Bean
    @Scope("prototype")
    public NativeEngineDetailsHandler nativeEngineDetailsHandler() {
        return new NativeEngineDetailsHandler();
    }
}
