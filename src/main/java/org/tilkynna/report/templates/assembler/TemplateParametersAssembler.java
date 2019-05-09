/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.templates.assembler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.openapitools.model.SelectionChoice;
import org.openapitools.model.TemplateParameter;
import org.springframework.stereotype.Component;
import org.tilkynna.engine.model.TemplateEngineParameter;
import org.tilkynna.engine.model.TemplateEngineParameterSelectionChoice;

@Component
public class TemplateParametersAssembler {

    public TemplateParameter templateEngineParameterToTemplateParameter(TemplateEngineParameter templateEngineParameter) {
        TemplateParameter templateParameter = new TemplateParameter();
        templateParameter.setName(templateEngineParameter.getName());
        templateParameter.setHelpText(templateEngineParameter.getHelpText());
        templateParameter.setPromptText(templateEngineParameter.getPromptText());
        templateParameter.setDisplayFormat(templateEngineParameter.getDisplayFormat());
        templateParameter.setDefaultValue(templateEngineParameter.getDefaultValue());
        templateParameter.setDataType(templateEngineParameter.getDataType());
        templateParameter.setControlType(templateEngineParameter.getControlType());
        templateParameter.setGroup(templateEngineParameter.getGroup());
        templateParameter.setMandatory(templateEngineParameter.getMandatory());
        templateParameter.setAllowMultipleValues(templateEngineParameter.getAllowMutipleValues());

        List<TemplateEngineParameterSelectionChoice> engineSelectionChoices = templateEngineParameter.getSelectionList();
        if (engineSelectionChoices != null) {
            List<SelectionChoice> selectionList = new ArrayList<>();
            for (Iterator<TemplateEngineParameterSelectionChoice> iterator = engineSelectionChoices.iterator(); iterator.hasNext();) {
                TemplateEngineParameterSelectionChoice templateEngineParameterSelectionChoice = iterator.next();

                SelectionChoice selectionChoice = new SelectionChoice();
                selectionChoice.setLabel(templateEngineParameterSelectionChoice.getLabel());
                selectionChoice.setValue(templateEngineParameterSelectionChoice.getValue());

                selectionList.add(selectionChoice);
            }

            templateParameter.setSelectionList(selectionList);
        }

        return templateParameter;
    }

    public TemplateEngineParameter templateParameterToTemplateEngineParameter(TemplateParameter templateParameter) {
        return TemplateEngineParameter.builder()
                .controlType(templateParameter.getControlType())
                .name(templateParameter.getName())
                .helpText(templateParameter.getHelpText())
                .promptText(templateParameter.getPromptText())
                .displayFormat(templateParameter.getDisplayFormat())
                .defaultValue(templateParameter.getDefaultValue())
                .dataType(templateParameter.getDataType())
                .group(templateParameter.getGroup())
                .mandatory(templateParameter.getMandatory())
                .allowMutipleValues(templateParameter.getAllowMultipleValues())
                .build();
    }

    public List<TemplateParameter> templateEngineParameterToTemplateParameter(List<TemplateEngineParameter> parameters) {
        return parameters.stream()
                .map(this::templateEngineParameterToTemplateParameter)
                .collect(Collectors.toList());
    }
}
