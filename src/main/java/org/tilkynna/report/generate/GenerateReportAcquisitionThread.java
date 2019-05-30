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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.tilkynna.report.generate.model.db.GeneratedReportEntityRepository;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Transactional
public class GenerateReportAcquisitionThread {

    @Autowired
    private GeneratedReportEntityRepository generatedReportEntityRepository;

    @Autowired
    private GenerateReportQueueHandler generateReportQueueHandler;

    @Autowired
    private Executor generateReportTaskExecutor;

    public void asyncGetListStringGenerateReportRequests() {
        // TODO check generateReportTaskExecutor.size if and x % then don't try start any more new tasks
        List<String> correlationIds = generatedReportEntityRepository.findReportRequestsCorrelationIdsEnqueue();

        if (correlationIds != null && correlationIds.size() > 1) {

            for (Iterator<String> iterator = correlationIds.iterator(); iterator.hasNext();) {
                String correlationIdStr = iterator.next();
                UUID correlationId = UUID.fromString(correlationIdStr);

                log.info(String.format("generateReportTaskExecutor.size [%s]", //
                        ((ThreadPoolTaskExecutor) generateReportTaskExecutor).getThreadPoolExecutor().getQueue().size()));

                log.info(String.format("Start picked up by scheduler correlationId [%s] on Thread [%s]", correlationId, Thread.currentThread().getName()));
                generateReportQueueHandler.generateReportAsync(correlationId);
                log.info(String.format("End picked up by scheduler correlationId [%s] on Thread [%s]", correlationId, Thread.currentThread().getName()));

            }

            generatedReportEntityRepository.flush();

        }

        log.info("scanGenerateReportRequests END: {}", Thread.currentThread().getName());
    }
}