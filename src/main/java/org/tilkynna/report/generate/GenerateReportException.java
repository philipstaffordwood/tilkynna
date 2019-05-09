/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.generate;

public class GenerateReportException extends RuntimeException {
    private static final long serialVersionUID = -1144640836390440160L;

    private final String message;

    public GenerateReportException(String msg) {
        this.message = msg;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
