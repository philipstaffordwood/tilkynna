/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.common.error;

public class CustomValidationException extends RuntimeException {

    private static final long serialVersionUID = -7785553460847810795L;

    private final String message;

    public CustomValidationException(String msg) {
        this.message = msg;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
