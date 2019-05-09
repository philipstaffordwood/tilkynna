/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.common.error;

public class TemplateEngineExceptions {
    private TemplateEngineExceptions() {

    }

    public static class TempalteHasNoDatasourcesException extends TemplateEngineException {
        private static final long serialVersionUID = -8666734138814916303L;

        public TempalteHasNoDatasourcesException(String msg) {
            super("Failed to get Parameters for templateId: '" + msg + "'");
        }
    }

    public static class ParameterDefsExtractException extends TemplateEngineException {
        private static final long serialVersionUID = 5876996069229494859L;

        public ParameterDefsExtractException(String msg) {
            super("Failed to get Parameters for templateId: '" + msg + "'");
        }
    }
}
