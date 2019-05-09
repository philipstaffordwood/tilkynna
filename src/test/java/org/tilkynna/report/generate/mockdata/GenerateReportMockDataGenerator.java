/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.generate.mockdata;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.openapitools.model.DestinationOptions;
import org.openapitools.model.ExportFormat;
import org.openapitools.model.ReportStatus;
import org.openapitools.model.TemplateGenerateRemoteRequestBase;
import org.tilkynna.report.destination.model.db.DestinationParameterEntity;
import org.tilkynna.report.destination.model.db.SelectedDestinationParameterEntity;
import org.tilkynna.report.generate.model.db.ExportFormatEntity;
import org.tilkynna.report.generate.model.db.GeneratedReportEntity;
import org.tilkynna.report.generate.model.db.ReportStatusEntity;

public class GenerateReportMockDataGenerator {

    private GenerateReportMockDataGenerator() {

    }

    public static final TemplateGenerateRemoteRequestBase setupTemplateGenerateRemoteRequestBase(UUID destinationId) {
        TemplateGenerateRemoteRequestBase generateReportRequest = new TemplateGenerateRemoteRequestBase();
        generateReportRequest.setCallbackUrl("http://www.google.com");
        generateReportRequest.doNotRetry(true);
        generateReportRequest.setExportFormat(ExportFormat.PDF);

        DestinationOptions destinationOptions = new DestinationOptions();
        destinationOptions.destinationId(destinationId);
        // destination parameters

        generateReportRequest.setDestinationOptions(destinationOptions);

        return generateReportRequest;
    }

    public static final ReportStatus setupReportStatus(UUID correlationId) {
        ReportStatus reportStatus = new ReportStatus();
        reportStatus.setCorrelationId(correlationId);

        return reportStatus;
    }

    public static final GeneratedReportEntity setupGeneratedReportEntity() {
        ExportFormatEntity exportFormat = new ExportFormatEntity();
        exportFormat.setName("PDF");
        exportFormat.setMediaType("application/pdf");

        Set<SelectedDestinationParameterEntity> selectedDestinationParameters = new HashSet<>();
        DestinationParameterEntity destinationParameter = new DestinationParameterEntity();
        destinationParameter.setName("path");
        SelectedDestinationParameterEntity selectedParam = new SelectedDestinationParameterEntity();
        selectedParam.setDestinationParameter(destinationParameter);
        selectedParam.setValue("aFolderName");
        selectedDestinationParameters.add(selectedParam);

        GeneratedReportEntity generatedReportEntity = new GeneratedReportEntity();
        generatedReportEntity.setCorrelationId(UUID.randomUUID());
        generatedReportEntity.setRequestedBy(UUID.randomUUID());
        generatedReportEntity.setReportStatus(ReportStatusEntity.PENDING);
        generatedReportEntity.setExportFormat(exportFormat);
        generatedReportEntity.setSelectedDestinationParameters(selectedDestinationParameters);

        return generatedReportEntity;
    }

}
