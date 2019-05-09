/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.common.error;

public class ResourceNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 3945724551102619852L;

    private final String message;

    public ResourceNotFoundException(String msg) {
        this.message = msg;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
