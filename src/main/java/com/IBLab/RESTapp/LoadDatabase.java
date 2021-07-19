package com.IBLab.RESTapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Configuration
class LoadDatabase {

    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean
    CommandLineRunner initDatabase(ModuleRepository repository) {
    // SpringBoot runs CLrunner bean once app context is loaded
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return args -> {
            log.info("Preload module: " +
                    repository.save(new Module("Obi Wan", LocalDate.parse("01.01.3277", formatter))));
            log.info("Database initiated.");
        };
    }
}