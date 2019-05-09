/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.generate;

import org.openapitools.model.ReportStatus;

public class GenerateReportExceptions {
    private GenerateReportExceptions() {

    }

    public static class GenerateReportRequestToJsonException extends GenerateReportException {
        private static final long serialVersionUID = 1L;

        public GenerateReportRequestToJsonException(String msg) {
            super("Issues parsing JSON to Object: '" + msg + "'");
        }
    }

    public static class ReportDatasourceExceptionException extends GenerateReportException {
        private static final long serialVersionUID = 1L;

        public ReportDatasourceExceptionException(String msg) {
            super("Issue with datasource: '" + msg + "'");
        }
    }

    public static class IOException extends GenerateReportException {
        private static final long serialVersionUID = 1L;

        public IOException(String msg) {
            super("Failed to read generated report for: '" + msg + "'");
        }
    }

    public static class ReportNotReadyException extends GenerateReportException {
        private static final long serialVersionUID = 1L;

        private ReportStatus reportStatus;

        private ReportNotReadyException() {
            super("");
            reportStatus = null;
        }

        public ReportNotReadyException(ReportStatus reportStatus) {
            super("");
            this.reportStatus = reportStatus;
        }

        public ReportStatus getReportStatus() {
            return reportStatus;
        }
    }
}
