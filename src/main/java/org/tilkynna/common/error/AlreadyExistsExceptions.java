/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.common.error;

public class AlreadyExistsExceptions {

    private AlreadyExistsExceptions() {

    }

    public static class Datasource extends AlreadyExistsException {
        private static final long serialVersionUID = 1590960330093595987L;

        public Datasource(String msg) {
            super("Datasource already exists for: '" + msg + "'");
        }
    }

    public static class Destination extends AlreadyExistsException {
        private static final long serialVersionUID = 1L;

        public Destination(String msg) {
            super("Destination already exists for: '" + msg + "'");
        }
    }

    public static class Template extends AlreadyExistsException {
        private static final long serialVersionUID = -470142005199812103L;

        public Template(String templateName) {
            super("templateName already exists for: '" + templateName + "'");
        }
    }
}
