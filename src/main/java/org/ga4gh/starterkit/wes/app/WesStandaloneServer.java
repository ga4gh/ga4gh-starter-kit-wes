package org.ga4gh.starterkit.wes.app;

import org.ga4gh.starterkit.wes.beanconfig.StarterKitWesSpringConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Contains main method for running a standalone WES deployment as a Spring Boot application
 */
@Configuration
@EnableAutoConfiguration
@Import({
    WesStandaloneSpringConfig.class,
    StarterKitWesSpringConfig.class
})
@ComponentScan(basePackages = {
    "org.ga4gh.starterkit.wes.controller"
})
public class WesStandaloneServer {

    /**
     * Run the WES standalone server as a Spring Boot application.
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(WesStandaloneServer.class, args);
    }
}
