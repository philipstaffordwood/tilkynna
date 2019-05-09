/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.engine.model;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TemplateEngineParameter {

    /**
     * Unique parameter name
     */
    private String name;

    /**
     * Help text for this parameter
     */
    private String helpText;

    /**
     * Friendly name of parameter to show users
     */
    private String promptText;

    /**
     * Format to use when displaying parameter
     */
    private String displayFormat;

    /**
     * Default value for this parameter
     */
    private String defaultValue;

    /**
     * Data type of the parameter, options include:
     */
    private String dataType;

    /**
     * UI Control type of the parameter, options include:
     */
    private String controlType;

    /**
     * Grouping for display purposes of parameter
     */
    private String group;

    /**
     * Parameter mandatory or not
     */
    private Boolean mandatory;

    private Boolean allowMutipleValues;

    private List<TemplateEngineParameterSelectionChoice> selectionList;

}
