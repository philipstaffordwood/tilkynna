/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.engine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tilkynna.ReportingConstants;
import org.tilkynna.common.error.CustomValidationExceptions;

@Component
public class TemplateEngineFactory {

    @Autowired
    private BirtTemplateEngine birtRreportService;

    public TemplateEngine getReportService(String designFileName) {
        String designFileExtension = ReportingConstants.extractFileExtension(designFileName);

        if (designFileExtension == null) {
            throw new CustomValidationExceptions.TemplateFileExtensionNotAllowedException(designFileExtension);
        }
        if (designFileExtension.equalsIgnoreCase(ReportingConstants.BIRT_DESIGN_FILE_EXTENSION)) {
            return birtRreportService;
        } // TODO allows for other report engine eg: JasperReports, Crystal Report?

        return null;
    }

    public void setBirtRreportService(BirtTemplateEngine birtRreportService) {
        this.birtRreportService = birtRreportService;
    }
}
