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
public class GenerateReportTaskExecutorConfig /* implements AsyncConfigurer */ {

    // Override the Executor at the Method Level and not at 'Override the Executor at the Application Level'
    // so that I can configure differently

    @Value("${tilkynna.generate.threading.poolSize}")
    int poolSize;

    @Value("${tilkynna.generate.threading.maxPoolSize}")
    int maxPoolSize;

    @Value("${tilkynna.generate.threading.queueCapacity}")
    int queueCapacity;

    @Bean(name = "generateReportTaskExecutor1")
    public Executor getAsyncExecutor1() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadPriority(Thread.MIN_PRIORITY); //
        executor.setThreadNamePrefix("GenReport1-");
        executor.setCorePoolSize(poolSize);
        // executor.setMaxPoolSize(maxPoolSize);
        executor.setMaxPoolSize(Integer.MAX_VALUE);
        executor.setQueueCapacity(queueCapacity);
        // executor.setQueueCapacity(Integer.MAX_VALUE);
        executor.initialize();

        return executor;
    }

    // @Override
    @Bean(name = "generateReportTaskExecutor")
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadPriority(Thread.MIN_PRIORITY);
        executor.setThreadNamePrefix("GenReport-");
        executor.setCorePoolSize(poolSize);
        // executor.setMaxPoolSize(maxPoolSize);
        executor.setMaxPoolSize(Integer.MAX_VALUE);
        executor.setQueueCapacity(queueCapacity);
        // executor.setQueueCapacity(Integer.MAX_VALUE);
        executor.initialize();

        return executor;
    }
}