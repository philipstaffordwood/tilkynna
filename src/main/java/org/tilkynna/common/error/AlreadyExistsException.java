/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.common.error;

public abstract class AlreadyExistsException extends RuntimeException {

    private static final long serialVersionUID = 8070953247494557157L;

    private final String message;

    public AlreadyExistsException(String msg) {
        this.message = msg;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
