package org.tilkynna.report.generate.worker;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.tilkynna.report.generate.GenerateReportQueueHandler;
import org.tilkynna.report.generate.model.db.GeneratedReportEntityRepository;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class GenerateReportQueueRunnable implements Runnable {
    @Autowired
    private GeneratedReportEntityRepository generatedReportEntityRepository;

    @Autowired
    private GenerateReportQueueHandler generateReportQueueHandler;

    @Autowired
    private Executor generateReportTaskExecutor;

    boolean running = true;

    @Override
    public void run() {
        while (running) {
            log.info(String.format("Start Thread [%s]", Thread.currentThread().getName()));

            List<String> correlationIds = generatedReportEntityRepository.findReportRequestsToEnqueueS();

            if (correlationIds != null) {
                generatedReportEntityRepository.flush();

                for (Iterator<String> iterator = correlationIds.iterator(); iterator.hasNext();) {
                    String correlationIdStr = iterator.next();
                    UUID correlationId = UUID.fromString(correlationIdStr);

                    log.error(String.format("generateReportTaskExecutor.size [%s]", //
                            ((ThreadPoolTaskExecutor) generateReportTaskExecutor).getThreadPoolExecutor().getQueue().size()));
                    log.info(String.format("Start picked up by scheduler correlationId [%s] on Thread [%s]", correlationId, Thread.currentThread().getName()));
                    // generateReportQueueHandler.generateReport(correlationId);
                    generateReportQueueHandler.generateReportAsync(correlationId);
                    log.info(String.format("End picked up by scheduler correlationId [%s] on Thread [%s]", correlationId, Thread.currentThread().getName()));
                }
            }
        }
    }

    // @Override
    // public void run() {
    // while (running) {
    // log.info(String.format("Start Thread [%s]", Thread.currentThread().getName()));
    //
    // List<String> correlationIds = generatedReportEntityRepository.findReportRequestsToEnqueueS();
    //
    // if (correlationIds != null) {
    // generatedReportEntityRepository.flush();
    //
    // for (Iterator<String> iterator = correlationIds.iterator(); iterator.hasNext();) {
    // String correlationIdStr = iterator.next();
    // UUID correlationId = UUID.fromString(correlationIdStr);
    //
    // log.info(String.format("Start picked up by scheduler correlationId [%s] on Thread [%s]", correlationId, Thread.currentThread().getName()));
    // generateReportQueueHandler.generateReport(correlationId);
    // log.info(String.format("End picked up by scheduler correlationId [%s] on Thread [%s]", correlationId, Thread.currentThread().getName()));
    // }
    // }
    // }
    // }

    // @Override
    // public void run() {
    // while (running) {
    // log.info(String.format("Start Thread [%s]", Thread.currentThread().getName()));
    // String correlationIdStr = generatedReportEntityRepository.findReportRequestsToEnqueueViaUpdateSet();
    //
    // if (correlationIdStr != null) {
    // UUID correlationId = UUID.fromString(correlationIdStr);
    // generateReportQueueHandler.generateReport(correlationId);
    // log.info(String.format("Report picked up by GenerateReportQueueHandler correlationId [%s] on Thread [%s]", correlationId, Thread.currentThread().getName()));
    // generatedReportEntityRepository.flush();
    // }
    // }
    // }

    @PreDestroy
    public void destroy() {
        running = false;
    }
}