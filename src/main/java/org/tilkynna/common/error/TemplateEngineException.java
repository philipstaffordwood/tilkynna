/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.common.error;

public class TemplateEngineException extends RuntimeException {

    private static final long serialVersionUID = 4569899462251656674L;
    private final String message;

    public TemplateEngineException(String msg) {
        this.message = msg;
    }

    @Override
    public String getMessage() {
        return message;
    }

}