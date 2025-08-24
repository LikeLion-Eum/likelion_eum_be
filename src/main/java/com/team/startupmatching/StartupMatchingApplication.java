package com.team.startupmatching;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(
        exclude = {
                SecurityAutoConfiguration.class,
                UserDetailsServiceAutoConfiguration.class
        }
)
@org.springframework.boot.context.properties.ConfigurationPropertiesScan
public class StartupMatchingApplication {
    public static void main(String[] args) {
        SpringApplication.run(StartupMatchingApplication.class, args);
    }
}
