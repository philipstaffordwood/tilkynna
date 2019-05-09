/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.generate.model.db;

import org.springframework.data.jpa.domain.Specification;

public class GeneratedReportEntitySpecBuilder {

    private GeneratedReportEntitySpecBuilder() {

    }

    public static Specification<GeneratedReportEntity> getPendingReportRequests() {
        // TODO include sorting by async vs. sync first
        return GeneratedReportEntitySpecBuilder.generatedReportSpec(ReportStatusEntity.PENDING);
    }

    private static Specification<GeneratedReportEntity> generatedReportSpec(ReportStatusEntity status) {
        Specification<GeneratedReportEntity> statusSpec = GeneratedReportEntitySpecBuilder.reportStatus(status);

        return Specification.where(statusSpec);
    }

    private static Specification<GeneratedReportEntity> reportStatus(ReportStatusEntity reportStatus) {
        return (datasource, cq, cb) -> cb.equal(datasource.get("reportStatus"), reportStatus);
    }

}
