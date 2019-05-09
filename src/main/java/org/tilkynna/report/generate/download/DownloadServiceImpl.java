/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.generate.download;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.openapitools.model.ExportFormat;
import org.openapitools.model.LookupExportFormat;
import org.openapitools.model.ReportStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.tilkynna.ReportingConstants;
import org.tilkynna.common.error.ResourceNotFoundExceptions;
import org.tilkynna.report.destination.provider.DestinationProviderFactory;
import org.tilkynna.report.destination.provider.StreamDestinationProvider;
import org.tilkynna.report.generate.GenerateReportExceptions;
import org.tilkynna.report.generate.assembler.GeneratedReportAssembler;
import org.tilkynna.report.generate.model.db.ExportFormatEntity;
import org.tilkynna.report.generate.model.db.ExportFormatEntityRepository;
import org.tilkynna.report.generate.model.db.GeneratedReportEntity;
import org.tilkynna.report.generate.model.db.GeneratedReportEntityRepository;

@Service
public class DownloadServiceImpl implements DownloadService {

    @Autowired
    private GeneratedReportEntityRepository generatedReportEntityRepository;

    @Autowired
    private ExportFormatEntityRepository exportFormatEntityRepository;

    @Autowired
    private DestinationProviderFactory destinationProviderFactory;

    @Autowired
    private GeneratedReportAssembler generatedReportAssembler;

    @Override
    public String getContentType(ExportFormat exportFormat) {
        ExportFormatEntity exportFormatEntity = exportFormatEntityRepository.findByNameIgnoreCase(exportFormat.name()) //
                .orElseThrow(() -> new ResourceNotFoundExceptions.ExportFormat(exportFormat.name()));

        return exportFormatEntity.getMediaType();
    }

    @Override
    public List<LookupExportFormat> listFileExportFormats() {
        List<LookupExportFormat> lookupExportFormats = new ArrayList<LookupExportFormat>();

        ExportFormat[] exportFormats = ExportFormat.values();
        for (ExportFormat exportFormat : exportFormats) {
            LookupExportFormat lookupExportFormat = new LookupExportFormat();
            lookupExportFormat.setName(exportFormat);
            lookupExportFormat.setMediatype(getContentType(exportFormat));

            lookupExportFormats.add(lookupExportFormat);
        }

        return lookupExportFormats;
    }

    @Override
    public ReportStatus getReportStatus(UUID correlationId) {
        GeneratedReportEntity generatedReport = generatedReportEntityRepository.findById(correlationId).orElseThrow(() -> new ResourceNotFoundExceptions.GeneratedReportEntity(correlationId.toString()));

        return generatedReportAssembler.mapGeneratedReportEntityToReportStatus(generatedReport);
    }

    @Override
    public GeneratedReportEntity findById(UUID correlationId) {
        return generatedReportEntityRepository.findById(correlationId) //
                .orElseThrow(() -> new ResourceNotFoundExceptions.GeneratedReportEntity(correlationId.toString()));
    }

    @Override
    public Resource downloadReportOrGetStatus(GeneratedReportEntity generatedReport) {

        if (!generatedReport.isFinished()) {
            ReportStatus status = generatedReportAssembler.mapGeneratedReportEntityToReportStatus(generatedReport);
            throw new GenerateReportExceptions.ReportNotReadyException(status);
        }

        StreamDestinationProvider destinationProvider = (StreamDestinationProvider) destinationProviderFactory.get(ReportingConstants.REPORT_OUTPUTTO_STREAM);
        Resource downloadedFile = null;
        try {
            downloadedFile = destinationProvider.read(generatedReport.getCorrelationId());
        } catch (IOException e) {
            throw new GenerateReportExceptions.IOException(generatedReport.getCorrelationId().toString());
        }

        return downloadedFile;
    }

}
