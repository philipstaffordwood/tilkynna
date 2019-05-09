/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna;

public class ReportingConstants {

    private ReportingConstants() {
    }

    public static final String BIRT_DESIGN_FILE_EXTENSION = "rptdesign";
    public static final String VALID_FILE_EXTENSIONS = BIRT_DESIGN_FILE_EXTENSION;

    public static final String GENERATE_REPORT_RESPONSE = "generateReportResponse";

    // output to options
    public static final String REPORT_OUTPUTTO_STREAM = "STREAM";
    public static final String REPORT_OUTPUTTO_FOLDER = "FOLDER";
    public static final String SFTP = "SFTP";
    public static final String SMTP = "SMTP";

    // output formats
    public static final String REPORT_OUTPUTFORMAT_PDF = "PDF";
    public static final String REPORT_OUTPUTFORMAT_XLSX = "XLSX";
    public static final String REPORT_OUTPUTFORMAT_CSV = "CSV";
    public static final String REPORT_OUTPUTFORMAT_HTML = "HTML";

    public static final String extractFileExtension(String fileName) {
        int designFileExtensionStart = fileName.lastIndexOf('.');

        return fileName.substring(designFileExtensionStart + 1, fileName.length());
    }
}
