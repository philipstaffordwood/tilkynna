/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.generate;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import javax.transaction.Transactional;

import org.eclipse.birt.core.exception.BirtException;
import org.openapitools.model.DestinationOptions;
import org.openapitools.model.ExportFormat;
import org.openapitools.model.ReportStatus;
import org.openapitools.model.TemplateGenerateRemoteRequestBase;
import org.openapitools.model.TemplateParameterValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.tilkynna.common.error.CustomValidationExceptions;
import org.tilkynna.common.error.ResourceNotFoundExceptions;
import org.tilkynna.common.utils.ApplicationInstance;
import org.tilkynna.engine.TemplateEngine;
import org.tilkynna.engine.TemplateEngineFactory;
import org.tilkynna.report.datasource.model.db.DatasourceEntity;
import org.tilkynna.report.destination.model.dao.DestinationEntityRepository;
import org.tilkynna.report.destination.model.db.DestinationEntity;
import org.tilkynna.report.destination.model.db.DestinationParameterEntity;
import org.tilkynna.report.destination.model.db.SelectedDestinationParameterEntity;
import org.tilkynna.report.generate.assembler.GeneratedReportAssembler;
import org.tilkynna.report.generate.model.db.ExportFormatEntity;
import org.tilkynna.report.generate.model.db.ExportFormatEntityRepository;
import org.tilkynna.report.generate.model.db.GeneratedReportEntity;
import org.tilkynna.report.generate.model.db.GeneratedReportEntityRepository;
import org.tilkynna.report.generate.model.db.ReportStatusEntity;
import org.tilkynna.report.generate.retry.GenerateReportRetryPolicyImpl;
import org.tilkynna.report.templates.TemplateEntity;
import org.tilkynna.report.templates.TemplateEntityRepository;
import org.tilkynna.report.templates.TemplateStorageProperties;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class GenerateReportServiceImpl implements GenerateReportService {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ExportFormatEntityRepository exportFormatEntityRepository;

    @Autowired
    private GeneratedReportEntityRepository generatedReportEntityRepository;

    @Autowired
    private TemplateEntityRepository templateRepository;

    @Autowired
    private DestinationEntityRepository destinationRepository;

    @Autowired
    private TemplateEngineFactory templateEngineFactory;

    @Autowired
    private GeneratedReportAssembler generatedReportAssembler;

    @Autowired
    private GenerateReportRetryPolicyImpl generateReportRetryPolicy;

    private final Path rootLocation;

    @Autowired
    public GenerateReportServiceImpl(TemplateStorageProperties properties) {
        this.rootLocation = Paths.get(properties.getLocation());
    }

    private DestinationEntity getDestination(UUID destinationIdRequest) {
        return destinationRepository.findById(destinationIdRequest) //
                .orElseThrow(() -> new ResourceNotFoundExceptions.Destination(destinationIdRequest.toString()));
    }

    private TemplateEntity getTemplate(UUID templateId) {
        return templateRepository.findById(templateId) //
                .orElseThrow(() -> new ResourceNotFoundExceptions.Template(templateId.toString()));
    }

    @Async("reportRequestThreadPool") // Spring's @Async annotation, indicating it will run on a separate thread.
    @Transactional
    @Override
    public CompletableFuture<ReportStatus> initiateGenerateReportAsync(UUID templateId, UUID requestedBy, TemplateGenerateRemoteRequestBase templateGenerateRemoteRequestBase) {
        ExportFormat exportFormatRequest = templateGenerateRemoteRequestBase.getExportFormat();
        DestinationOptions destinationOptions = templateGenerateRemoteRequestBase.getDestinationOptions();
        UUID destinationId = destinationOptions.getDestinationId(); // destinationId will never be null this is validated on entry into endpoint

        TemplateEntity templateEntity = getTemplate(templateId);
        DestinationEntity destinationEntity = getDestination(destinationId);

        validateGenerateReportRequest(templateEntity, destinationEntity, destinationOptions);

        ExportFormatEntity exportFormat = exportFormatEntityRepository.findByNameIgnoreCase(exportFormatRequest.name()) //
                .orElseThrow(() -> new ResourceNotFoundExceptions.ExportFormat(exportFormatRequest.name()));

        GeneratedReportEntity generatedReportEntity = new GeneratedReportEntity();
        generatedReportEntity.setTemplate(templateEntity);
        generatedReportEntity.setRequestBody(requestObjToJson(templateId, templateGenerateRemoteRequestBase));
        generatedReportEntity.setRequestedBy(requestedBy);
        generatedReportEntity.setReportStatus(ReportStatusEntity.PENDING);
        generatedReportEntity.setExportFormat(exportFormat);
        generatedReportEntity.setDestination(destinationEntity);
        generatedReportEntity.setRetryCount(generateReportRetryPolicy.defaultRetryCount(templateGenerateRemoteRequestBase.getDoNotRetry()));

        setupSelectedDestinationParameters(destinationOptions, destinationEntity, generatedReportEntity);

        generatedReportEntity.setProccesedBy(String.format("Instance [%s] on Thread [%s]", ApplicationInstance.name(), Thread.currentThread().getName()));
        generatedReportEntityRepository.save(generatedReportEntity);

        return CompletableFuture.completedFuture(generatedReportAssembler.mapGeneratedReportEntityToReportStatus(generatedReportEntity));

    }

    private void setupSelectedDestinationParameters(DestinationOptions destinationOptions, DestinationEntity destinationEntity, GeneratedReportEntity generatedReportEntity) {
        List<TemplateParameterValue> destinationParameters = destinationOptions.getDestinationParameters();

        if (destinationParameters != null) {
            Set<SelectedDestinationParameterEntity> selectedDestinationParameters = new HashSet<SelectedDestinationParameterEntity>();
            destinationParameters.forEach(destParam -> { //
                DestinationParameterEntity destinationParameterEntity = destinationEntity.getDestinationParameterEntity(destParam.getName());
                SelectedDestinationParameterEntity param = new SelectedDestinationParameterEntity(destinationParameterEntity, generatedReportEntity, destParam.getValue());

                selectedDestinationParameters.add(param);
            });
            generatedReportEntity.setSelectedDestinationParameters(selectedDestinationParameters);
        }
    }

    private void validateGenerateReportRequest(TemplateEntity templateEntity, DestinationEntity destinationEntity, DestinationOptions destinationOptions) {
        if (templateEntity.hasInActiveDatasources()) {
            throw new CustomValidationExceptions.TemplateHasInactiveDatasourcesException(templateEntity.getId());
        }

        if (!destinationEntity.isActive()) {
            throw new CustomValidationExceptions.DestinationInactiveException(destinationEntity.getDestinationId());
        }

        List<TemplateParameterValue> destinationParameters = destinationOptions.getDestinationParameters();
        if (destinationParameters != null) {
            StringBuilder invalidDestinationParameters = new StringBuilder();
            destinationParameters.forEach(destParam -> { //
                DestinationParameterEntity destinationParameterEntity = destinationEntity.getDestinationParameterEntity(destParam.getName());
                if (destinationParameterEntity == null) {
                    invalidDestinationParameters.append(destParam.getName() + ", ");
                }
            });

            if (invalidDestinationParameters.length() > 0) {
                throw new CustomValidationExceptions.InvalidDestinationParameterException(destinationEntity.getDestinationId(), invalidDestinationParameters.toString());
            }
        }
    }

    private String requestObjToJson(UUID templateId, TemplateGenerateRemoteRequestBase templateGenerateRemoteRequestBase) {
        try {
            return objectMapper.writeValueAsString(templateGenerateRemoteRequestBase);
        } catch (JsonProcessingException e) {
            throw new GenerateReportExceptions.GenerateReportRequestToJsonException(templateId.toString());
        }
    }

    @Override
    public byte[] generateReport(UUID templateId, TemplateGenerateRemoteRequestBase templateGenerateRemoteRequestBase) throws ParseException, BirtException {
        TemplateEntity templateEntity = getTemplate(templateId);

        TemplateEngine templateEngine = templateEngineFactory.getReportService(templateEntity.getOriginalFilename());

        HashMap<String, String> reportParams = new HashMap<>();

        List<TemplateParameterValue> requestParameters = templateGenerateRemoteRequestBase.getReportParameters();
        for (Iterator<TemplateParameterValue> iterator = requestParameters.iterator(); iterator.hasNext();) {
            TemplateParameterValue templateParameterValue = iterator.next();

            reportParams.put(templateParameterValue.getName(), templateParameterValue.getValue());
        }

        Set<DatasourceEntity> datasources = templateEntity.getDatasources();
        // there is only every 1 at the moment

        // TODO is pathToTemplateFile still needed?
        Path pathToTemplateFile = this.rootLocation.resolve(templateId.toString());

        String exportFormat = templateGenerateRemoteRequestBase.getExportFormat().name();
        return templateEngine.generateReport(exportFormat, reportParams, pathToTemplateFile.toString(), datasources);

    }
}
