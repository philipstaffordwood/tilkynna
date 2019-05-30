/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.generate;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.tilkynna.report.generate.model.db.GeneratedReportEntityRepository;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Transactional
public class GenerateReportJobsAcquirer {

    @Autowired
    private GeneratedReportEntityRepository generatedReportRepository;

    @Autowired
    private GenerateReportHandler generateReportHandler;

    @Autowired
    @Qualifier("generateReportThreadPoolExecutor")
    private Executor generateReportExecutor;

    public void getPendingJobsAndPushToGenerateReportThreadPool() {
        log.info("getPendingJobsAndPushToGenerateReportThreadPool START: {}", Thread.currentThread().getName());

        ThreadPoolExecutor generateReportThreadPool = ((ThreadPoolTaskExecutor) generateReportExecutor).getThreadPoolExecutor();
        // TODO check generateReportTaskExecutor.size if and x % then don't try start any more new tasks
        List<String> correlationIds = generatedReportRepository.getBatchOfPendingGenerateReportJobs();

        boolean correlationIdsExist = correlationIds != null && correlationIds.size() > 1;
        if (correlationIdsExist) {

            for (Iterator<String> iterator = correlationIds.iterator(); iterator.hasNext();) {
                String correlationIdStr = iterator.next();
                UUID correlationId = UUID.fromString(correlationIdStr);

                log.info(String.format("generateReportTaskExecutor.size [%s]", generateReportThreadPool.getQueue().size()));

                log.info(String.format("Start picked up by scheduler correlationId [%s] on Thread [%s]", correlationId, Thread.currentThread().getName()));
                generateReportHandler.pushGenerateReportToThreadPoolForProcessing(correlationId);
                log.info(String.format("End picked up by scheduler correlationId [%s] on Thread [%s]", correlationId, Thread.currentThread().getName()));

            }

            generatedReportRepository.flush();
        }

        log.info("getPendingJobsAndPushToGenerateReportThreadPool END: {}", Thread.currentThread().getName());
    }
}