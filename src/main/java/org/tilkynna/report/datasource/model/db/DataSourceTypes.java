/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.datasource.model.db;

public enum DataSourceTypes {
    JDBC(Values.JDBC, true, "Java Database Connectivity for accessing DBs"), FLAT_FILE(Values.FLAT_FILE, false, "Text files for example CSV");

    private DataSourceTypes(String val, boolean implemented, String description) {
        // force equality between name of enum instance, and value of constant
        if (!this.name().equals(val)) {
            throw new IllegalArgumentException("Incorrect use of DataSourceTypes");
        }

        this.setImplemented(implemented);
        this.setDescription(description);
    }

    public boolean isImplemented() {
        return implemented;
    }

    private void setImplemented(boolean implemented) {
        this.implemented = implemented;
    }

    public String getDescription() {
        return description;
    }

    private void setDescription(String description) {
        this.description = description;
    }

    private boolean implemented = false;
    private String description;

    public static class Values {
        private Values() {
        }

        public static final String JDBC = "JDBC";
        public static final String FLAT_FILE = "FLAT_FILE";
    }

}
