/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.generate;

import java.util.UUID;

import org.openapitools.api.ReportsApi;
import org.openapitools.model.ExportFormat;
import org.openapitools.model.ReportStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.tilkynna.report.generate.GenerateReportExceptions.ReportNotReadyException;
import org.tilkynna.report.generate.download.DownloadService;
import org.tilkynna.report.generate.model.db.ExportFormatEntity;
import org.tilkynna.report.generate.model.db.GeneratedReportEntity;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@PreAuthorize("hasRole('TILKYNNA_USER') or hasRole('TILKYNNA_ADMIN')")
public class ReportsApiController implements ReportsApi {

    @Autowired
    private DownloadService downloadService;

    // https://www.callicoder.com/spring-boot-file-upload-download-rest-api-example/
    @Override
    public ResponseEntity<Resource> downloadReportOrGetStatus(UUID correlationId) {
        GeneratedReportEntity generatedReportEntity = downloadService.findById(correlationId);
        Resource resource = downloadService.downloadReportOrGetStatus(generatedReportEntity);
        ExportFormatEntity exportFormatEntity = generatedReportEntity.getExportFormat();
        String contentType = downloadService.getContentType(ExportFormat.fromValue(exportFormatEntity.getName()));

        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)) //
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"") //
                .body(resource);
    }

    @ResponseBody
    @ExceptionHandler({ ReportNotReadyException.class })
    @ResponseStatus(HttpStatus.ACCEPTED)
    protected ResponseEntity<ReportStatus> handleReportNotReady(ReportNotReadyException ex) {
        return new ResponseEntity<>(ex.getReportStatus(), HttpStatus.ACCEPTED);
    }

    @Override
    public ResponseEntity<ReportStatus> getReportStatus(UUID correlationId) {
        return new ResponseEntity<>(downloadService.getReportStatus(correlationId), HttpStatus.OK);
    }
}
