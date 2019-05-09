/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.common.error;

public class ResourceNotFoundExceptions {

    private ResourceNotFoundExceptions() {

    }

    public static class Template extends ResourceNotFoundException {

        private static final long serialVersionUID = 2019613513693525692L;

        public Template(String templateId) {
            super("no template for id: '" + templateId + "'");
        }
    }

    public static class Datasource extends ResourceNotFoundException {
        private static final long serialVersionUID = 1590960330093595987L;

        public Datasource(String msg) {
            super("Datasource not found: '" + msg + "'");
        }
    }

    public static class Destination extends ResourceNotFoundException {

        private static final long serialVersionUID = 2019613513693525692L;

        public Destination(String destinationId) {
            super("no destination for id: '" + destinationId + "'");
        }
    }

    public static class GeneratedReportEntity extends ResourceNotFoundException {

        private static final long serialVersionUID = 1L;

        public GeneratedReportEntity(String correlationId) {
            super("Report Request not found: '" + correlationId + "'");
        }
    }

    public static class ExportFormat extends ResourceNotFoundException {

        private static final long serialVersionUID = 1L;

        public ExportFormat(String exportFormat) {
            super("ExportFormat not supported: '" + exportFormat + "'");
        }
    }
}
