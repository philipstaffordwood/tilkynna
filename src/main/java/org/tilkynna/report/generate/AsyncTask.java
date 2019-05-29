package org.tilkynna.report.generate;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.tilkynna.report.generate.model.db.GeneratedReportEntity;
import org.tilkynna.report.generate.model.db.GeneratedReportEntityRepository;
import org.tilkynna.report.generate.model.db.ReportStatusEntity;
import org.tilkynna.report.generate.retry.GenerateReportRetryPolicyImpl;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Transactional
public class AsyncTask {

    @Autowired
    private GeneratedReportEntityRepository generatedReportEntityRepository;

    @Autowired
    private GenerateReportQueueHandler generateReportQueueHandler;

    @Autowired
    private GenerateReportRetryPolicyImpl generateReportRetryPolicy;

    @Async("generateReportTaskExecutor1")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void asyncGetOneModelGenerateReportRequests() {
        GeneratedReportEntity reportRequest = generatedReportEntityRepository.findReportRequestsToEnqueue();

        if (reportRequest != null) {
            // log.info("scanGenerateReportRequests START debug 2: {}", Thread.currentThread().getName());
            log.info(String.format("Start picked up by scheduler correlationId [%s] on Thread [%s]", reportRequest.getCorrelationId(), Thread.currentThread().getName()));
            generateReportQueueHandler.generateReportAsync(reportRequest);
            log.info(String.format("End picked up by scheduler correlationId [%s] on Thread [%s]", reportRequest.getCorrelationId(), Thread.currentThread().getName()));

            reportRequest.setRetryCount(generateReportRetryPolicy.calculateRetryCount(reportRequest.getRetryCount()));
            reportRequest.setReportStatus(ReportStatusEntity.STARTED);
            generatedReportEntityRepository.save(reportRequest);
        }

        log.info("scanGenerateReportRequests END: {}", Thread.currentThread().getName());
    }

    @Async("generateReportTaskExecutor1")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void asyncGetListModelGenerateReportRequests() {
        List<GeneratedReportEntity> reportRequests = generatedReportEntityRepository.findReportRequestsToEnqueueGroups();

        log.info("scanGenerateReportRequests START: {}", Thread.currentThread().getName());
        if (reportRequests != null) {
            for (Iterator<GeneratedReportEntity> iterator = reportRequests.iterator(); iterator.hasNext();) {
                GeneratedReportEntity reportRequest = iterator.next();

                log.info(String.format("Start picked up by scheduler correlationId [%s] on Thread [%s]", reportRequest.getCorrelationId(), Thread.currentThread().getName()));
                generateReportQueueHandler.generateReportAsync(reportRequest);
                log.info(String.format("End picked up by scheduler correlationId [%s] on Thread [%s]", reportRequest.getCorrelationId(), Thread.currentThread().getName()));

                reportRequest.setRetryCount(generateReportRetryPolicy.calculateRetryCount(reportRequest.getRetryCount()));
                reportRequest.setReportStatus(ReportStatusEntity.STARTED);
                generatedReportEntityRepository.save(reportRequest);
            }

        }
        log.info("scanGenerateReportRequests END: {}", Thread.currentThread().getName());
    }

    @Async("generateReportTaskExecutor1")
    public void asyncGetListStringGenerateReportRequests() {
        List<String> correlationIds = generatedReportEntityRepository.findReportRequestsToEnqueueS();

        if (correlationIds != null && correlationIds.size() > 1) {

            for (Iterator<String> iterator = correlationIds.iterator(); iterator.hasNext();) {
                String correlationIdStr = iterator.next();
                UUID correlationId = UUID.fromString(correlationIdStr);

                log.info(String.format("Start picked up by scheduler correlationId [%s] on Thread [%s]", correlationId, Thread.currentThread().getName()));
                generateReportQueueHandler.generateReport(correlationId);
                log.info(String.format("End picked up by scheduler correlationId [%s] on Thread [%s]", correlationId, Thread.currentThread().getName()));

            }

            generatedReportEntityRepository.flush();

        }

        log.info("scanGenerateReportRequests END: {}", Thread.currentThread().getName());
    }
}