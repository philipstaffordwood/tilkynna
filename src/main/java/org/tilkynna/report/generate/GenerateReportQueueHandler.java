/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.generate;

import java.io.IOException;
import java.text.ParseException;
import java.util.UUID;

import org.eclipse.birt.core.exception.BirtException;
import org.openapitools.model.TemplateGenerateRemoteRequestBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHandlingException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.tilkynna.common.error.CustomValidationExceptions;
import org.tilkynna.common.error.CustomValidationExceptions.TemplateHasInactiveDatasourcesException;
import org.tilkynna.common.error.ResourceNotFoundExceptions;
import org.tilkynna.report.destination.model.dao.DestinationEntityRepository;
import org.tilkynna.report.destination.model.db.DestinationEntity;
import org.tilkynna.report.destination.provider.DestinationProvider;
import org.tilkynna.report.destination.provider.DestinationProviderFactory;
import org.tilkynna.report.generate.GenerateReportExceptions.ReportDatasourceExceptionException;
import org.tilkynna.report.generate.model.db.GeneratedReportEntity;
import org.tilkynna.report.generate.model.db.GeneratedReportEntityRepository;
import org.tilkynna.report.generate.model.db.ReportStatusEntity;
import org.tilkynna.report.templates.TemplateEntity;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

//https://www.devglan.com/spring-boot/spring-boot-async-task-executor
@Slf4j
@Component
public class GenerateReportQueueHandler {
    @Autowired
    private GeneratedReportEntityRepository generatedReportEntityRepository;

    @Autowired
    private DestinationEntityRepository destinationEntityRepository;

    @Autowired
    private GenerateReportService generateReportService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DestinationProviderFactory destinationProviderFactory;

    @Async("generateReportTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void generateReportAsync(UUID correlationId) {
        log.info(String.format("Report picked up by GenerateReportQueueHandler correlationId [%s] on Thread [%s]", correlationId, Thread.currentThread().getName()));

        GeneratedReportEntity generatedReportEntity = generatedReportEntityRepository.findById(correlationId) //
                .orElseThrow(() -> new ResourceNotFoundExceptions.GeneratedReportEntity(correlationId.toString()));

        try {
            validateGenerateReportRequest(generatedReportEntity);
            byte[] generatedReport = generateReport(generatedReportEntity);
            writeReportToDestination(generatedReportEntity, generatedReportEntity.getDestination(), generatedReport);

            generatedReportEntity.setReportStatus(ReportStatusEntity.FINISHED);

            // TODO call the callback URL

        } catch (IOException | ParseException | BirtException | ReportDatasourceExceptionException | TemplateHasInactiveDatasourcesException knownException) {
            log.info(String.format("knownException Generating Report correlationId [%s] on Thread [%s]", generatedReportEntity.getCorrelationId(), Thread.currentThread().getName()));
            log.error("knownException: {}", knownException.getMessage());

            generatedReportEntity.setReportStatus(ReportStatusEntity.FAILED);

        } catch (MessageHandlingException e) { // TODO can I handle connection failures to destination in a cleaner way?
            // JSchException ..
            // ((org.springframework.messaging.MessageHandlingException)unknownException).getMostSpecificCause()
            log.info(String.format("knownException MessageHandlingException Generating Report correlationId [%s] on Thread [%s]", generatedReportEntity.getCorrelationId(), Thread.currentThread().getName()));
            log.error("knownException MessageHandlingException: {}", e.getMessage());

            generatedReportEntity.setReportStatus(ReportStatusEntity.FAILED);
        } catch (Exception unknownException) {
            log.info(String.format("unknownException Generating Report correlationId [%s] on Thread [%s]", generatedReportEntity.getCorrelationId(), Thread.currentThread().getName()));
            log.error("unknownException: {}", unknownException.getMessage());

            generatedReportEntity.setReportStatus(ReportStatusEntity.FAILED);
        }

        log.info(String.format("start update status for ReportEntity correlationId [%s] on Thread [%s]", correlationId, Thread.currentThread().getName()));
        generatedReportEntityRepository.save(generatedReportEntity);
        log.info(String.format("end update status for ReportEntity correlationId [%s] on Thread [%s]", correlationId, Thread.currentThread().getName()));
    }

    // @Async("generateReportTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void generateReport(UUID correlationId) {
        log.info(String.format("Report picked up by GenerateReportQueueHandler correlationId [%s] on Thread [%s]", correlationId, Thread.currentThread().getName()));

        GeneratedReportEntity generatedReportEntity = generatedReportEntityRepository.findById(correlationId) //
                .orElseThrow(() -> new ResourceNotFoundExceptions.GeneratedReportEntity(correlationId.toString()));

        try {
            validateGenerateReportRequest(generatedReportEntity);
            byte[] generatedReport = generateReport(generatedReportEntity);
            writeReportToDestination(generatedReportEntity, generatedReportEntity.getDestination(), generatedReport);

            generatedReportEntity.setReportStatus(ReportStatusEntity.FINISHED);

            // TODO call the callback URL

        } catch (IOException | ParseException | BirtException | ReportDatasourceExceptionException | TemplateHasInactiveDatasourcesException knownException) {
            log.info(String.format("knownException Generating Report correlationId [%s] on Thread [%s]", generatedReportEntity.getCorrelationId(), Thread.currentThread().getName()));
            log.error("knownException: {}", knownException.getMessage());

            generatedReportEntity.setReportStatus(ReportStatusEntity.FAILED);

        } catch (MessageHandlingException e) { // TODO can I handle connection failures to destination in a cleaner way?
            // JSchException ..
            // ((org.springframework.messaging.MessageHandlingException)unknownException).getMostSpecificCause()
            log.info(String.format("knownException MessageHandlingException Generating Report correlationId [%s] on Thread [%s]", generatedReportEntity.getCorrelationId(), Thread.currentThread().getName()));
            log.error("knownException MessageHandlingException: {}", e.getMessage());

            generatedReportEntity.setReportStatus(ReportStatusEntity.FAILED);
        } catch (Exception unknownException) {
            log.info(String.format("unknownException Generating Report correlationId [%s] on Thread [%s]", generatedReportEntity.getCorrelationId(), Thread.currentThread().getName()));
            log.error("unknownException: {}", unknownException.getMessage());

            generatedReportEntity.setReportStatus(ReportStatusEntity.FAILED);
        }

        log.info(String.format("start update status for ReportEntity correlationId [%s] on Thread [%s]", correlationId, Thread.currentThread().getName()));
        generatedReportEntityRepository.save(generatedReportEntity);
        log.info(String.format("end update status for ReportEntity correlationId [%s] on Thread [%s]", correlationId, Thread.currentThread().getName()));
    }

    @Async("generateReportTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void generateReportAsync(GeneratedReportEntity reportRequest) {
        log.info(String.format("Report picked up by GenerateReportQueueHandler correlationId [%s] on Thread [%s]", reportRequest.getCorrelationId(), Thread.currentThread().getName()));

        GeneratedReportEntity generatedReportEntity = generatedReportEntityRepository.findById(reportRequest.getCorrelationId()) //
                .orElseThrow(() -> new ResourceNotFoundExceptions.GeneratedReportEntity(reportRequest.getCorrelationId().toString()));

        DestinationEntity destinationEntity = destinationEntityRepository.findById(reportRequest.getDestination().getDestinationId()) //
                .orElseThrow(() -> new ResourceNotFoundExceptions.GeneratedReportEntity(reportRequest.getCorrelationId().toString()));

        try {
            validateGenerateReportRequest(generatedReportEntity);
            byte[] generatedReport = generateReport(generatedReportEntity);
            writeReportToDestination(reportRequest, destinationEntity, generatedReport);

            generatedReportEntity.setReportStatus(ReportStatusEntity.FINISHED);

            // TODO call the callback URL

        } catch (IOException | ParseException | BirtException | ReportDatasourceExceptionException | TemplateHasInactiveDatasourcesException knownException) {
            log.info(String.format("knownException Generating Report correlationId [%s] on Thread [%s]", generatedReportEntity.getCorrelationId(), Thread.currentThread().getName()));
            log.error("knownException: {}", knownException.getMessage());

            generatedReportEntity.setReportStatus(ReportStatusEntity.FAILED);

        } catch (MessageHandlingException e) { // TODO can I handle connection failures to destination in a cleaner way?
            // JSchException ..
            // ((org.springframework.messaging.MessageHandlingException)unknownException).getMostSpecificCause()
            log.info(String.format("knownException MessageHandlingException Generating Report correlationId [%s] on Thread [%s]", generatedReportEntity.getCorrelationId(), Thread.currentThread().getName()));
            log.error("knownException MessageHandlingException: {}", e.getMessage());

            generatedReportEntity.setReportStatus(ReportStatusEntity.FAILED);
        } catch (Exception unknownException) {
            log.info(String.format("unknownException Generating Report correlationId [%s] on Thread [%s]", generatedReportEntity.getCorrelationId(), Thread.currentThread().getName()));
            log.error("unknownException: {}", unknownException.getMessage());

            generatedReportEntity.setReportStatus(ReportStatusEntity.FAILED);
        }

        log.info(String.format("start update status for ReportEntity correlationId [%s] on Thread [%s]", reportRequest.getCorrelationId(), Thread.currentThread().getName()));
        generatedReportEntityRepository.save(generatedReportEntity);
        log.info(String.format("end update status for ReportEntity correlationId [%s] on Thread [%s]", reportRequest.getCorrelationId(), Thread.currentThread().getName()));
    }

    private void writeReportToDestination(GeneratedReportEntity reportRequest, DestinationEntity destinationEntity, byte[] generatedReport) throws IOException {
        log.info(String.format("Generating Report start writing to destination correlationId [%s] on Thread [%s]", reportRequest.getCorrelationId(), Thread.currentThread().getName()));
        DestinationProvider destinationProvider = destinationProviderFactory.get(destinationEntity.getType());
        destinationProvider.write(reportRequest, generatedReport);
        log.info(String.format("Generating Report end writing to destination correlationId [%s] on Thread [%s]", reportRequest.getCorrelationId(), Thread.currentThread().getName()));
    }

    private byte[] generateReport(GeneratedReportEntity generatedReportEntity) throws IOException, JsonParseException, JsonMappingException, ParseException, BirtException {
        log.info(String.format("Generating Report start generating report correlationId [%s] on Thread [%s]", generatedReportEntity.getCorrelationId(), Thread.currentThread().getName()));
        TemplateGenerateRemoteRequestBase templateGenerateRemoteRequestBase = objectMapper.readValue(generatedReportEntity.getRequestBody(), TemplateGenerateRemoteRequestBase.class);
        byte[] generatedReport = generateReportService.generateReport(generatedReportEntity.getTemplate().getId(), templateGenerateRemoteRequestBase);
        log.info(String.format("Generating Report end generating report correlationId [%s] on Thread [%s]", generatedReportEntity.getCorrelationId(), Thread.currentThread().getName()));

        return generatedReport;
    }

    // validate request again in case changes have been made since original request
    private void validateGenerateReportRequest(GeneratedReportEntity generatedReportEntity) {
        log.info(String.format("Validating Generating Report correlationId [%s] on Thread [%s]", generatedReportEntity.getCorrelationId(), Thread.currentThread().getName()));
        TemplateEntity templateEntity = generatedReportEntity.getTemplate();
        DestinationEntity destinationEntity = generatedReportEntity.getDestination();
        if (templateEntity.hasInActiveDatasources()) {
            throw new CustomValidationExceptions.TemplateHasInactiveDatasourcesException(templateEntity.getId());
        }

        if (!destinationEntity.isActive()) {
            throw new CustomValidationExceptions.DestinationInactiveException(destinationEntity.getDestinationId());
        }
    }
}
