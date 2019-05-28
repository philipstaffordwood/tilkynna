package org.tilkynna.report.generate;

import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
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
    public void asyncMethod() {
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
    public void asyncMethod2() {
        List<GeneratedReportEntity> reportRequests = generatedReportEntityRepository.findReportRequestsToEnqueueGroups();

        if (reportRequests != null) {
            // reportRequests.forEach(r -> r.setReportStatus(ReportStatusEntity.STARTED));
            // generatedReportEntityRepository.saveAll(reportRequests);

            for (Iterator<GeneratedReportEntity> iterator = reportRequests.iterator(); iterator.hasNext();) {
                GeneratedReportEntity reportRequest = iterator.next();
                // log.info("scanGenerateReportRequests START debug 2: {}", Thread.currentThread().getName());
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
}