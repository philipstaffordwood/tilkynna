/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.lookup.driver;

public class DriversLocationException extends RuntimeException {
    private static final long serialVersionUID = -6302533235382517737L;

    private final String message;

    public DriversLocationException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
