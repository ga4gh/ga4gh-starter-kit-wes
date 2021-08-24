package org.ga4gh.starterkit.wes.app;

import org.apache.commons.cli.Options;
import org.ga4gh.starterkit.common.util.webserver.ServerPropertySetter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Contains main method for running a standalone WES deployment as a Spring Boot application
 */
@SpringBootApplication
@ComponentScan(basePackages = "org.ga4gh.starterkit.wes")
public class WesServer {

    /**
     * Run the WES standalone server as a Spring Boot application.
     * @param args command line arguments
     */
    public static void main(String[] args) {
        boolean setupSuccess = setup(args);
        if (setupSuccess) {
            SpringApplication.run(WesServer.class, args);
        } else {
            System.out.println("Application failed at initial setup phase, this is likely an error in the YAML config file. Exiting");
        }
    }

    private static boolean setup(String[] args) {
        Options options = new WesServerSpringConfig().getCommandLineOptions();
        return ServerPropertySetter.setServerProperties(WesServerYamlConfigContainer.class, args, options, "config");
    }
}
