/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.engine.model;

import lombok.Getter;

@Getter
public class TemplateEngineParameterSelectionChoice {
    private String label;
    private Object value;

    public TemplateEngineParameterSelectionChoice(String label, Object value) {
        this.label = label;
        this.value = value;
    }
}
