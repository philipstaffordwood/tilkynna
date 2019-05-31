/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.generate.processengine;

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
public class GenerateReportScheduler {

    @Autowired
    private GeneratedReportEntityRepository generatedReportEntityRepository;

    @Autowired
    private GenerateReportRetryPolicyImpl generateReportRetryPolicy;

    @Autowired
    private GenerateReportJobsAcquirer generateReportJobsAcquirer; // https://stackoverflow.com/questions/48233445/spring-boot-scheduled-not-running-in-different-threads

    /**
     * Retrieves jobs from the database (generated_report table) that <br/>
     * will be pushed onto the GenerateReportThreadPoolQueue for executed next.<br/>
     * 
     * Scheduler execution doesn’t wait for the completion of the previous execution. <br/>
     * Starts on a new run every fixedRateString milliseconds (as each run is on its own thread & don't interact with each other)
     * 
     * Transactional(propagation = Propagation.REQUIRES_NEW); starts the new transaction <br/>
     * Call .. generateReportQueueHandler.generateReport(reportRequest); asynchronously so that we can <br/>
     * update status to STARTED in this transaction and commit for others to see. <br/>
     */
    // https://stackoverflow.com/questions/48233445/spring-boot-scheduled-not-running-in-different-threads
    @Scheduled(fixedRateString = "${tilkynna.generate.monitorPendingRequests.fixedRateInMilliseconds}", //
            initialDelayString = "${tilkynna.generate.monitorPendingRequests.initialDelayInMilliseconds}")
    public void acquireGenerateReportJobsInPendingStatus() {
        generateReportJobsAcquirer.getPendingJobsAndPushToGenerateReportThreadPool();
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
