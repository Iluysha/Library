package com.epam.library;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LibraryApplication implements ApplicationRunner {
    private static final Logger logger = LogManager.getLogger(LibraryApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(LibraryApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments applicationArguments) {
        logger.debug("App is configured");
    }
}
