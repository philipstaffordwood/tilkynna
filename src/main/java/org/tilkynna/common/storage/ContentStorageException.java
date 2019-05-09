/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.common.storage;

public class ContentStorageException extends RuntimeException {

    private static final long serialVersionUID = 7927067730656869797L;

    public ContentStorageException(String message) {
        super(message);
    }

    public ContentStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
