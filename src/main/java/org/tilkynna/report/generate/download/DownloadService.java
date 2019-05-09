/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.generate.download;

import java.util.List;
import java.util.UUID;

import org.openapitools.model.ExportFormat;
import org.openapitools.model.LookupExportFormat;
import org.openapitools.model.ReportStatus;
import org.springframework.core.io.Resource;
import org.tilkynna.report.generate.model.db.GeneratedReportEntity;

public interface DownloadService {

    public String getContentType(ExportFormat exportFormat);

    public List<LookupExportFormat> listFileExportFormats();

    public ReportStatus getReportStatus(UUID correlationId);

    public GeneratedReportEntity findById(UUID correlationId);

    public Resource downloadReportOrGetStatus(GeneratedReportEntity generatedReport);
}
