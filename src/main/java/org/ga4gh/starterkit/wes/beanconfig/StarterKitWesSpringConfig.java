package org.ga4gh.starterkit.wes.beanconfig;

import org.ga4gh.starterkit.common.config.DatabaseProps;
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
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.context.annotation.RequestScope;

@Configuration
@ConfigurationProperties
public class StarterKitWesSpringConfig {

    @Bean
    public WesHibernateUtil wesHibernateUtil(
        @Autowired DatabaseProps databaseProps
    ) {
        WesHibernateUtil hibernateUtil = new WesHibernateUtil();
        hibernateUtil.setDatabaseProps(databaseProps);
        return hibernateUtil;
    }

    @Bean
    @RequestScope
    public SubmitRunRequestHandler submitRunRequestHandler() {
        return new SubmitRunRequestHandler();
    }

    @Bean
    @RequestScope
    public GetRunLogRequestHandler getRunLogRequestHandler() {
        return new GetRunLogRequestHandler();
    }

    @Bean
    @RequestScope
    public GetRunStatusRequestHandler getRunStatusRequestHandler() {
        return new GetRunStatusRequestHandler();
    }

    @Bean
    @RequestScope
    public NextflowTaskLogsRequestHandler nextflowTaskLogsRequestHandler() {
        return new NextflowTaskLogsRequestHandler();
    }

    @Bean
    @RequestScope
    public NextflowWorkflowLogsRequestHandler nextflowWorkflowLogsRequestHandler() {
        return new NextflowWorkflowLogsRequestHandler();
    }

    @Bean
    public RunManagerFactory runManagerFactory() {
        return new RunManagerFactory();
    }

    @Bean
    @Scope("prototype")
    public RunManager runManager() {
        return new RunManager();
    }

    @Bean
    @Scope("prototype")
    public NextflowTypeDetailsHandler nextflowTypeDetailsHandler() {
        return new NextflowTypeDetailsHandler();
    }

    @Bean
    @Scope("prototype")
    public NativeEngineDetailsHandler nativeEngineDetailsHandler() {
        return new NativeEngineDetailsHandler();
    }


}
