package org.ga4gh.starterkit.wes.app;

import java.util.HashSet;
import org.apache.commons.cli.Options;
import org.ga4gh.starterkit.common.util.webserver.ServerPropertySetter;
import org.ga4gh.starterkit.wes.config.engine.NativeEngineConfig;
import org.ga4gh.starterkit.wes.config.engine.SlurmEngineConfig;
import org.ga4gh.starterkit.wes.config.language.NextflowLanguageConfig;
import org.ga4gh.starterkit.wes.model.WorkflowEngine;
import org.ga4gh.starterkit.wes.model.WorkflowType;
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
        HashSet<Class<?>> atomicClasses = new HashSet<>();
        atomicClasses.add(WorkflowType.class);
        atomicClasses.add(WorkflowEngine.class);
        atomicClasses.add(NextflowLanguageConfig.class);
        atomicClasses.add(NativeEngineConfig.class);
        atomicClasses.add(SlurmEngineConfig.class);

        ServerPropertySetter setter = new ServerPropertySetter(true, atomicClasses);
        boolean success = setter.setServerProperties(WesServerYamlConfigContainer.class, args, options, "config");
        return success;
    }
}
