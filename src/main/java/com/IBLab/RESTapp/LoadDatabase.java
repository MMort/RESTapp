package com.IBLab.RESTapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class LoadDatabase {

    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean
    CommandLineRunner initDatabase(ModuleRepository repository) {
    // SpringBoot runs CLrunner bean once app context is loaded
        return args -> {
            log.info("Database initiated.");
        };
    }
}