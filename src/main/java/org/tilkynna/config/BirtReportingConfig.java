/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.tilkynna.engine.BirtEngineFactory;

@Configuration
public class BirtReportingConfig {
    @Bean
    protected BirtEngineFactory birtReportEngine() {
        return new BirtEngineFactory();
    }
}