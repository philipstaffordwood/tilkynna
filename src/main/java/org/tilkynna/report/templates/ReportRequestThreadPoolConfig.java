/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.templates;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * ThreadPool for initiateGenerateReportAsync this is to prioritise accepting GenerateReportRequests over actual generation of reports.
 */
@Configuration
public class ReportRequestThreadPoolConfig {

    @Bean(name = "reportRequestThreadPool")
    public Executor threadPoolTaskExecutor() {

        ThreadPoolTaskExecutor threadPoolTaskScheduler = new ThreadPoolTaskExecutor();

        threadPoolTaskScheduler.setThreadPriority(Thread.MAX_PRIORITY);
        threadPoolTaskScheduler.setThreadNamePrefix("GenReportRequest-");
        threadPoolTaskScheduler.initialize();
        return threadPoolTaskScheduler;
    }
}
