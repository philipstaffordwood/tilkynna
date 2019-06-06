/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.generate.processengine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
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

    @Value("${tilkynna.generate.threading.batchSize}")
    private int batchSize;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void getPendingJobsAndPushToGenerateReportThreadPool() {
        log.debug("getPendingJobsAndPushToGenerateReportThreadPool START: {}", Thread.currentThread().getName());

        ThreadPoolExecutor generateReportThreadPool = ((ThreadPoolTaskExecutor) generateReportExecutor).getThreadPoolExecutor();

        if (batchSize <= generateReportThreadPool.getQueue().remainingCapacity()) {
            List<String> correlationIds = generatedReportRepository.findReportRequestsToEnqueue(batchSize);
            boolean correlationIdsExist = correlationIds != null && correlationIds.size() >= 1;
            if (correlationIdsExist) {
                for (Iterator<String> iterator = correlationIds.iterator(); iterator.hasNext();) {
                    String correlationIdStr = iterator.next();
                    UUID correlationId = UUID.fromString(correlationIdStr);

                    log.debug(String.format("generateReportTaskExecutor.size [%s]", generateReportThreadPool.getQueue().size()));

                    log.debug(String.format("Start picked up by scheduler correlationId [%s] on Thread [%s]", correlationId, Thread.currentThread().getName()));
                    generateReportHandler.pushGenerateReportToThreadPoolForProcessing(correlationId);
                    log.debug(String.format("End picked up by scheduler correlationId [%s] on Thread [%s]", correlationId, Thread.currentThread().getName()));

                }

                List<UUID> correlationIdsAsUUIDs = new ArrayList<UUID>();
                correlationIds.forEach(c -> correlationIdsAsUUIDs.add(UUID.fromString(c)));
                generatedReportRepository.markAsStarted(correlationIdsAsUUIDs);
            }
        }

        log.debug("getPendingJobsAndPushToGenerateReportThreadPool END: {}", Thread.currentThread().getName());
    }
}