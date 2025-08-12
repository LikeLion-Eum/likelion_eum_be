package com.team.startupmatching;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@org.springframework.boot.context.properties.ConfigurationPropertiesScan
public class StartupMatchingApplication {

    public static void main(String[] args) {
        SpringApplication.run(StartupMatchingApplication.class, args);
    }

}
