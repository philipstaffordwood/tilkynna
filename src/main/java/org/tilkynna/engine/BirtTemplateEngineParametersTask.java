/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.engine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.birt.report.engine.api.IGetParameterDefinitionTask;
import org.eclipse.birt.report.engine.api.IParameterDefnBase;
import org.eclipse.birt.report.engine.api.IParameterGroupDefn;
import org.eclipse.birt.report.engine.api.IParameterSelectionChoice;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IScalarParameterDefn;
import org.eclipse.birt.report.model.api.CascadingParameterGroupHandle;
import org.eclipse.birt.report.model.api.ScalarParameterHandle;
import org.eclipse.birt.report.model.api.elements.DesignChoiceConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tilkynna.engine.model.TemplateEngineParameter;
import org.tilkynna.engine.model.TemplateEngineParameterSelectionChoice;

// Inspiration for this class taken from the following examples: 
// https://books.google.co.za/books?id=fSAIBVaGV2wC&pg=PA279&lpg=PA279&dq=IGetParameterDefinitionTask+task+%3D+engin.createGetParameterDefinitionTask(design);&source=bl&ots=irpdx1B9gL&sig=5a-cIphugOSF1gWwDOtSk8uwoOc&hl=en&sa=X&ved=0ahUKEwj63t71st_QAhUJLsAKHbN0B_MQ6AEILzAD#v=onepage&q&f=false
// https://www.eclipse.org/forums/index.php/t/238566/
// https://wiki.eclipse.org/Parameter_Details_(BIRT)_2.1
@Component
public class BirtTemplateEngineParametersTask {

    @Autowired
    private IReportEngine birtEngine;

    @SuppressWarnings("unchecked")
    public List<TemplateEngineParameter> getTemplateParameters(IReportRunnable reportDesign) {

        List<TemplateEngineParameter> reportParameters = new ArrayList<>();

        // Create Parameter Definition Task and retrieve parameter definitions
        IGetParameterDefinitionTask parameterDefTask = birtEngine.createGetParameterDefinitionTask(reportDesign);
        Collection<IParameterDefnBase> params = parameterDefTask.getParameterDefns(true);

        // Iterate over each parameter
        for (IParameterDefnBase param : params) {
            // group section found
            if (param instanceof IParameterGroupDefn) {
                IParameterGroupDefn group = (IParameterGroupDefn) param;

                List<IScalarParameterDefn> groupContents = group.getContents();
                groupContents.forEach(paramDef -> {
                    TemplateEngineParameter rReportParameter = loadParameterDetails(paramDef, parameterDefTask);
                    rReportParameter.setGroup(group.getName());
                    reportParameters.add(rReportParameter);
                });
            } else {
                // Parameters are not in a group
                IScalarParameterDefn scalar = (IScalarParameterDefn) param;
                TemplateEngineParameter rReportParameter = loadParameterDetails(scalar, parameterDefTask);
                rReportParameter.setGroup("DEFAULT");

                reportParameters.add(rReportParameter);
            }
        }

        return reportParameters;
    }

    private TemplateEngineParameter loadParameterDetails(IScalarParameterDefn paramDefn, IGetParameterDefinitionTask parameterDefTask) {
        TemplateEngineParameter reportParameter = TemplateEngineParameter.builder()
                .name(paramDefn.getName())
                .helpText(paramDefn.getHelpText())
                .promptText(paramDefn.getPromptText())
                .displayFormat(paramDefn.getDisplayFormat())
                .defaultValue(paramDefn.getDefaultValue())
                .mandatory(paramDefn.isRequired())
                .build();

        reportParameter.setControlType(extractContentType(paramDefn));
        reportParameter.setDataType(extractDataType(paramDefn));
        reportParameter.setAllowMutipleValues(extractAllowMultipleValues(paramDefn));

        // setting option values for list type fields eg: radio, combo
        ScalarParameterHandle parameterHandle = (ScalarParameterHandle) paramDefn.getHandle();
        if (paramDefn.getControlType() != IScalarParameterDefn.TEXT_BOX) {
            if (parameterHandle.getContainer() instanceof CascadingParameterGroupHandle) {
                // Cascading Parameters:: the list of values for one parameter depends on the value chosen in another parameter.

            } else {
                // Normal List eg: radio, combo
                @SuppressWarnings("unchecked")
                Collection<IParameterSelectionChoice> selectionList = parameterDefTask.getSelectionList(paramDefn.getName());
                List<TemplateEngineParameterSelectionChoice> choices = selectionList.stream()
                        .map(choice -> new TemplateEngineParameterSelectionChoice(choice.getLabel(), choice.getValue()))
                        .collect(Collectors.toList());

                reportParameter.setSelectionList(choices);
            }
        }
        return reportParameter;
    }

    private boolean extractAllowMultipleValues(IScalarParameterDefn paramDefn) {
        return DesignChoiceConstants.SCALAR_PARAM_TYPE_MULTI_VALUE.equals(paramDefn.getScalarParameterType());
    }

    private String extractContentType(IScalarParameterDefn scalar) {
        switch (scalar.getControlType()) {
        case IScalarParameterDefn.LIST_BOX:
            return "LIST_BOX";
        case IScalarParameterDefn.TEXT_BOX:
            return "TEXT_BOX";
        case IScalarParameterDefn.CHECK_BOX:
            return "CHECK_BOX";
        case IScalarParameterDefn.RADIO_BUTTON:
            return "RADIO_BUTTON";
        default:
            return "TEXT_BOX";
        }
    }

    private String extractDataType(IScalarParameterDefn scalar) {
        switch (scalar.getDataType()) {
        case IScalarParameterDefn.TYPE_ANY:
            return "Any";
        case IScalarParameterDefn.TYPE_STRING:
            return "String";
        case IScalarParameterDefn.TYPE_FLOAT:
            return "Float";
        case IScalarParameterDefn.TYPE_DECIMAL:
            return "Decimal";
        case IScalarParameterDefn.TYPE_DATE:
            return "Date";
        case IScalarParameterDefn.TYPE_DATE_TIME:
            return "DateTime";
        case IScalarParameterDefn.TYPE_INTEGER:
            if (scalar.getControlType() == IScalarParameterDefn.LIST_BOX) {
                return "Object[] Integer";
            } else {
                return "Integer";
            }
        case IScalarParameterDefn.TYPE_BOOLEAN:
            return "Boolean";
        case IScalarParameterDefn.TYPE_TIME:
            return "Time";
        default:
            return "String";
        }
    }

}
