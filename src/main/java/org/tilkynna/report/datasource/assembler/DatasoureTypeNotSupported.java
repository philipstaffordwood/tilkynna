/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.datasource.assembler;

public class DatasoureTypeNotSupported extends RuntimeException {

    private static final long serialVersionUID = -813058799880242820L;

    public DatasoureTypeNotSupported(String msg) {
        super(msg);
    }
}
