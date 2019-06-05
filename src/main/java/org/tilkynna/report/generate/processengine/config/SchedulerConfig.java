/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.generate.processengine.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

/**
 * By default, all the @Scheduled tasks are executed in a default thread pool of size one created by Spring. <br/>
 * Overriding this to allow for bigger pool size for GenerateReportQueueScheduler
 * 
 * @author melissap
 */
@Configuration
public class SchedulerConfig implements SchedulingConfigurer {

    /**
     * The pool size.
     */
    @Value("${tilkynna.scheduler.poolSize}")
    private final int POOL_SIZE = 4;

    /**
     * Configures the scheduler to allow multiple pools.
     *
     * @param scheduledTaskRegistrar
     *            The task registrar.
     */
    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();

        threadPoolTaskScheduler.setPoolSize(POOL_SIZE);
        threadPoolTaskScheduler.setThreadPriority(Thread.MIN_PRIORITY);
        threadPoolTaskScheduler.setThreadNamePrefix("scheduled-task-pool-");
        threadPoolTaskScheduler.initialize();

        scheduledTaskRegistrar.setTaskScheduler(threadPoolTaskScheduler);

    }

}