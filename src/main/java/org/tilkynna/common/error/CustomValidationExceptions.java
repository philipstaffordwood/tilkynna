/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.common.error;

import java.util.UUID;

import org.tilkynna.ReportingConstants;

public class CustomValidationExceptions {

    private CustomValidationExceptions() {

    }

    public static class TemplateEmptyException extends CustomValidationException {
        private static final long serialVersionUID = -3539620854589452197L;

        public TemplateEmptyException() {
            super("file cannot be empty");
        }
    }

    public static class TemplateFileExtensionNotAllowedException extends CustomValidationException {
        private static final long serialVersionUID = -3539620854589452197L;

        public TemplateFileExtensionNotAllowedException(String fileExtension) {
            super(String.format("File extension %s is not valid. Tilkynna currently only allows for extensions %s.", fileExtension, ReportingConstants.VALID_FILE_EXTENSIONS));
        }
    }

    public static class OneDatasourcePerTemplate extends CustomValidationException {
        private static final long serialVersionUID = 1590960330093595987L;

        public OneDatasourcePerTemplate() {
            super("Tilkynna currently only allows for 1 Datasource per Template");
        }
    }

    public static class TemplateHasInactiveDatasourcesException extends CustomValidationException {
        private static final long serialVersionUID = -8666734138814916303L;

        public TemplateHasInactiveDatasourcesException(UUID uuid) {
            super("Tempalte Has InActive Datasources templateId: '" + uuid + "'");
        }
    }

    public static class DestinationInactiveException extends CustomValidationException {
        private static final long serialVersionUID = -8666734138814916303L;

        public DestinationInactiveException(UUID uuid) {
            super("Destination is InActive destinationId: '" + uuid + "'");
        }
    }

    public static class InvalidDestinationParameterException extends CustomValidationException {
        private static final long serialVersionUID = -8666734138814916303L;

        public InvalidDestinationParameterException(UUID destinationId, String invalidDestinationParams) {
            super(String.format("Invalid destinationParameters %s for destinationId %s.", invalidDestinationParams, destinationId));
        }
    }

    public static class UpdateDestinationDestinationTypeNotEqual extends CustomValidationException {
        private static final long serialVersionUID = 1L;

        public UpdateDestinationDestinationTypeNotEqual(UUID destinationId, String destinationType) {
            super(String.format("You cannot update the destinationType for existing destination '%s', destinationType for this destination is %s.", destinationId, destinationType));
        }

    }

}
