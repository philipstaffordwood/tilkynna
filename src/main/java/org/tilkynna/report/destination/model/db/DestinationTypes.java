/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.destination.model.db;

public enum DestinationTypes {
    STREAM(Values.STREAM, true, null), //
    SFTP(Values.SFTP, true, "path"), //
    SMTP(Values.SMTP, false, "to,cc,bcc,subject,body"), //
    WEB(Values.WEB, false, "path"), //
    S3(Values.S3, false, "path");

    private DestinationTypes(String val, boolean implemented, String inputFields) {
        // force equality between name of enum instance, and value of constant
        if (!this.name().equals(val)) {
            throw new IllegalArgumentException("Incorrect use of DestinationTypes");
        }

        this.setImplemented(implemented);
        this.setInputFields(inputFields);
    }

    public boolean isImplemented() {
        return implemented;
    }

    private void setImplemented(boolean implemented) {
        this.implemented = implemented;
    }

    public String getInputFields() {
        return inputFields;
    }

    private void setInputFields(String inputFields) {
        this.inputFields = inputFields;
    }

    private boolean implemented = false;
    private String inputFields;

    public static class Values {
        private Values() {
        }

        public static final String STREAM = "STREAM";
        public static final String SFTP = "SFTP";
        public static final String SMTP = "SMTP";
        public static final String WEB = "WEB";
        public static final String S3 = "S3";

    }
}
