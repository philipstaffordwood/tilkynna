/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.common.storage;

public class ContentStorageNotFoundException extends ContentStorageException {

    private static final long serialVersionUID = 7773579119871653631L;

    public ContentStorageNotFoundException(String message) {
        super(message);
    }

    public ContentStorageNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}