/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.generate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.tilkynna.report.generate.model.db.GeneratedReportEntity;
import org.tilkynna.report.generate.model.db.GeneratedReportEntityRepository;
import org.tilkynna.report.generate.model.db.ReportStatusEntity;
import org.tilkynna.report.generate.retry.GenerateReportRetryPolicyImpl;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional
@Component
public class GenerateReportQueueScheduler {

    @Autowired
    private GeneratedReportEntityRepository generatedReportEntityRepository;

    @Autowired
    private GenerateReportQueueHandler generateReportQueueHandler;

    @Autowired
    private GenerateReportRetryPolicyImpl generateReportRetryPolicy;

    /**
     * Runs enqueued generate report requests <br/>
     * Scheduler execution doesn’t wait for the completion of the previous execution. <br/>
     * Starts on a new run every fixedRateString milliseconds (as each run is on its own thread & don't interact with each other)
     * 
     * Transactional(propagation = Propagation.REQUIRES_NEW); starts the new transaction <br/>
     * Call .. generateReportQueueHandler.generateReport(reportRequest); asynchronously so that we can <br/>
     * update status to STARTED in this transaction and commit for others to see. <br/>
     */
    @Scheduled(fixedRateString = "${tilkynna.generate.monitorPendingRequests.fixedRateInMilliseconds}", //
            initialDelayString = "${tilkynna.generate.monitorPendingRequests.initialDelayInMilliseconds}")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void scanGenerateReportRequests() {
        log.debug("scanGenerateReportRequests START debug: {}", Thread.currentThread().getName());

        GeneratedReportEntity reportRequest = generatedReportEntityRepository.findReportRequestsToEnqueue();
        log.debug("scanGenerateReportRequests reportRequests: " + reportRequest);

        if (reportRequest != null) {
            generateReportQueueHandler.generateReportAsync(reportRequest);

            reportRequest.setRetryCount(generateReportRetryPolicy.calculateRetryCount(reportRequest.getRetryCount()));
            reportRequest.setReportStatus(ReportStatusEntity.STARTED);
            generatedReportEntityRepository.save(reportRequest);
        }

        log.debug("scanGenerateReportRequests END: {}", Thread.currentThread().getName());
    }

    /**
     * Pickup FAILED report requests and re-queue these if retry_count is not zero. <br/>
     */
    @Scheduled(fixedRateString = "${tilkynna.generate.monitorFailedRequests.fixedRateInMilliseconds}", //
            initialDelayString = "${tilkynna.generate.monitorFailedRequests.initialDelayInMilliseconds}")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void scanFailedReportRequests() {
        log.debug("scanFailedReportRequests START debug: {}", Thread.currentThread().getName());

        GeneratedReportEntity reportRequest = generatedReportEntityRepository.findFailedReportRequestsToRetry(generateReportRetryPolicy.getBackOffPeriod());
        log.debug("scanFailedReportRequests reportRequests: " + reportRequest);

        if (reportRequest != null && generateReportRetryPolicy.isRetryNeeded(reportRequest.getRetryCount())) {

            reportRequest.setReportStatus(ReportStatusEntity.PENDING);
            generatedReportEntityRepository.save(reportRequest);
        }

        log.debug("scanFailedReportRequests END: {}", Thread.currentThread().getName());
    }

    // TODO include scanning for items stuck in the STARTED status
    // @Scheduled(fixedRateString = "${tilkynna.generate.monitorStalledRequests.fixedRateInMilliseconds}", //
    // initialDelayString = "${tilkynna.generate.monitorStalledRequests.initialDelayInMilliseconds}")
}
