package org.ga4gh.starterkit.wes.beanconfig;

import org.ga4gh.starterkit.common.config.DatabaseProps;
import org.ga4gh.starterkit.wes.utils.hibernate.WesHibernateUtil;
import org.ga4gh.starterkit.wes.utils.requesthandler.SubmitRunRequestHandler;
import org.ga4gh.starterkit.wes.utils.runlauncher.RunLauncherFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
    public RunLauncherFactory runLauncherFactory() {
        return new RunLauncherFactory();
    }
    
}
