/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.tilkynna.lookup.driver.DriversLocationProperties;
import org.tilkynna.report.destination.provider.StreamDestinationStorageProperties;
import org.tilkynna.report.templates.TemplateStorageProperties;
import org.tilkynna.security.config.SecurityProperties;

@EnableAsync
@EnableScheduling
@EnableIntegration
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class, UserDetailsServiceAutoConfiguration.class, MongoAutoConfiguration.class, MongoDataAutoConfiguration.class })
@EnableConfigurationProperties({ TemplateStorageProperties.class, DriversLocationProperties.class, StreamDestinationStorageProperties.class, SecurityProperties.class })
public class TilkynnaApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(TilkynnaApplication.class);

    public static void main(String... args) {
        LOGGER.info("Starting SpringApplication...");
        SpringApplication app = new SpringApplication(TilkynnaApplication.class);
        app.setBannerMode(Banner.Mode.CONSOLE);
        app.run();

        LOGGER.debug("This is an example debug message");
        LOGGER.info("This is an example info message");
        LOGGER.warn("This is an example warn message");
        LOGGER.error("This is an exmaple error message");

        LOGGER.info("SpringApplication has started...");

    }
}
