/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.generate.assembler;

import org.openapitools.model.ReportStatus;
import org.openapitools.model.ReportStatus.StatusEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tilkynna.report.generate.model.db.GeneratedReportEntity;
import org.tilkynna.report.generate.model.db.ReportStatusEntity;

@Component
public class GeneratedReportAssembler {

    @Value("${tilkynna.generate.hostname}")
    private String environmentHostName;

    public ReportStatus mapGeneratedReportEntityToReportStatus(GeneratedReportEntity generatedReport) {

        ReportStatusEntity reportStatusDB = generatedReport.getReportStatus();
        StatusEnum status = StatusEnum.valueOf(reportStatusDB.toString());

        ReportStatus reportStatus = new ReportStatus();
        reportStatus.correlationId(generatedReport.getCorrelationId());
        reportStatus.setStatus(status);
        reportStatus.setUrl(String.format("%s/reports/%s", environmentHostName, generatedReport.getCorrelationId()));

        return reportStatus;

    }
}
