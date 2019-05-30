/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.generate.config;

import java.util.concurrent.Executor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * ThreadPoolTaskExecutor for actual generation of report task.
 * 
 * @author melissap
 */
@Configuration
@EnableAsync
public class GenerateReportTaskExecutorConfig {

    @Value("${tilkynna.generate.threading.poolSize}")
    int poolSize;

    @Value("${tilkynna.generate.threading.maxPoolSize}")
    int maxPoolSize;

    @Value("${tilkynna.generate.threading.queueCapacity}")
    int queueCapacity;

    @Bean(name = "generateReportTaskExecutor")
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadPriority(Thread.MIN_PRIORITY);
        executor.setThreadNamePrefix("GenReportEngine-");
        executor.setCorePoolSize(poolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.initialize();

        return executor;
    }
}