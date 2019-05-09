/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.generate;

import java.text.ParseException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.eclipse.birt.core.exception.BirtException;
import org.openapitools.model.ReportStatus;
import org.openapitools.model.TemplateGenerateRemoteRequestBase;

public interface GenerateReportService {

    public CompletableFuture<ReportStatus> initiateGenerateReportAsync(UUID templateId, UUID requestedBy, TemplateGenerateRemoteRequestBase templateGenerateRemoteRequestBase);

    public byte[] generateReport(UUID templateId, TemplateGenerateRemoteRequestBase templateGenerateRemoteRequestBase) throws ParseException, BirtException;
}
