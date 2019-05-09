/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.templates;

public class TemplateNameNotEmptyException extends RuntimeException {

    private static final long serialVersionUID = -55012655760106827L;

    public TemplateNameNotEmptyException() {
        super("templateName cannot be null or empty");
    }
}
