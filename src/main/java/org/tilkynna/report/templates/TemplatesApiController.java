/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.templates;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.openapitools.api.TemplatesApi;
import org.openapitools.model.LookupTag;
import org.openapitools.model.ReportStatus;
import org.openapitools.model.Template;
import org.openapitools.model.TemplateDetail;
import org.openapitools.model.TemplateGenerateRemoteRequestBase;
import org.openapitools.model.TemplateTagList;
import org.openapitools.model.Templates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.tilkynna.common.error.CustomValidationExceptions;
import org.tilkynna.report.generate.GenerateReportService;
import org.tilkynna.security.SecurityContextUtility;

import lombok.extern.slf4j.Slf4j;

//TODO validate max file size for template upload

//https://spring.io/guides/gs/uploading-files/
@Slf4j
@RestController
@PreAuthorize("hasRole('TILKYNNA_USER') or hasRole('TILKYNNA_ADMIN')")
public class TemplatesApiController implements TemplatesApi {

    @Autowired
    private TemplateService templateService;

    @Autowired
    private GenerateReportService generateReportService;

    // TODO ... implementation of filterName/filterTags not done
    // TODO no pagination added for this endpoint (i wonder if there is a AOP way of doing it instead of having to include my custom pagination code?)
    @Override
    public ResponseEntity<Templates> getReportTemplates(Integer page, Integer size, String filterName, List<String> filterTags, List<String> orderBy) {

        return new ResponseEntity<>(templateService.findAll(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Template> uploadReportTemplate(MultipartFile file, String templateName, List<UUID> datasourceIds, List<String> tags) {
        if (file != null && file.isEmpty()) {
            throw new CustomValidationExceptions.TemplateEmptyException();
        }

        TemplateRequest templateRequest = new TemplateRequest();
        templateRequest.setFile(file);
        templateRequest.setTemplateName(templateName);
        templateRequest.setDatasourceIds(datasourceIds);
        templateRequest.setTags(tags);

        return new ResponseEntity<>(templateService.save(templateRequest), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<TemplateTagList> addTempalteTags(UUID templateId, List<LookupTag> lookupTags) {
        TemplateTagList tags = templateService.addTemplateTags(templateId, lookupTags);

        return new ResponseEntity<>(tags, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Void> removeAllTemplateTags(UUID templateId) {
        templateService.removeTempalteTags(templateId);

        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<TemplateDetail> getReportTemplate(UUID templateId) {
        return new ResponseEntity<>(templateService.getTemplateDetail(templateId), HttpStatus.OK);
    }

    // @Async //cannot use @Async as spring proxies created and then the swagger classes don't pickup correctly, and therefore there is no endpoint /templates
    @Override
    public ResponseEntity<ReportStatus> templateGenerateRemoteRequest(UUID templateId, TemplateGenerateRemoteRequestBase templateGenerateRemoteRequestBase) {
        UUID requestedBy = UUID.fromString(SecurityContextUtility.getUserIdFromJwt());
        CompletableFuture<ReportStatus> reportStatus = generateReportService.initiateGenerateReportAsync(templateId, requestedBy, templateGenerateRemoteRequestBase);

        try {
            ReportStatus reportStatusResponse = reportStatus.get();
            log.debug(String.format("GenerateRemoteRequest correlationId [%s] for templateId [%s] with request data: %s", //
                    reportStatusResponse.getCorrelationId(), templateId.toString(), templateGenerateRemoteRequestBase.toString()));

            return new ResponseEntity<>(reportStatusResponse, HttpStatus.CREATED); // to another thread BUT sync cause we need to respond to user with correlationId
        } catch (InterruptedException e) {
            log.error("Interrupted!", e);
            // Restore interrupted state...
            Thread.currentThread().interrupt();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        } catch (ExecutionException e) {
            throw ((RuntimeException) e.getCause());
        }
    }
}
