/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.engine;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.birt.report.engine.api.EXCELRenderOption;
import org.eclipse.birt.report.engine.api.EngineException;
import org.eclipse.birt.report.engine.api.HTMLRenderOption;
import org.eclipse.birt.report.engine.api.IEngineTask;
import org.eclipse.birt.report.engine.api.IGetParameterDefinitionTask;
import org.eclipse.birt.report.engine.api.IParameterDefnBase;
import org.eclipse.birt.report.engine.api.IParameterGroupDefn;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunAndRenderTask;
import org.eclipse.birt.report.engine.api.IScalarParameterDefn;
import org.eclipse.birt.report.engine.api.PDFRenderOption;
import org.eclipse.birt.report.engine.api.RenderOption;
import org.eclipse.birt.report.engine.emitter.csv.CSVRenderOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tilkynna.ReportingConstants;
import org.tilkynna.common.error.TemplateEngineExceptions;
import org.tilkynna.engine.model.TemplateEngineParameter;
import org.tilkynna.lookup.driver.DriversLocationProperties;
import org.tilkynna.report.datasource.model.db.DatasourceEntity;
import org.tilkynna.report.datasource.model.db.JDBCDatasourceEntity;
import org.tilkynna.report.generate.GenerateReportExceptions;

//https://spring.io/blog/2012/01/30/spring-framework-birt
@Service("rptdesign")
public class BirtTemplateEngine implements TemplateEngine {

    protected static final Log logger = LogFactory.getLog(BirtTemplateEngine.class);

    @Autowired
    private IReportEngine birtEngine;

    @Autowired
    private BirtTemplateEngineParametersTask birtTemplateEngineParametersTask;

    @Autowired
    private DriversLocationProperties driverLocationProperties;

    @Override
    public List<TemplateEngineParameter> getTemplateParameters(String templateId, String reportFileName) {
        IReportRunnable reportDesign;
        try {
            reportDesign = birtEngine.openReportDesign(reportFileName);
        } catch (EngineException e) {
            throw new TemplateEngineExceptions.ParameterDefsExtractException(templateId);
        }

        return birtTemplateEngineParametersTask.getTemplateParameters(reportDesign);
    }

    // TODO could the output be directly to destination within going via byte[] (can we use different out by destination ID? eg: FileOutputStream)
    @Override
    public byte[] generateReport(String outputFormat, Map<String, String> reportParams, String reportFileName, Set<DatasourceEntity> datasources) throws EngineException {
        IReportRunnable reportDesign = birtEngine.openReportDesign(reportFileName);
        IRunAndRenderTask renderTask = birtEngine.createRunAndRenderTask(reportDesign);
        renderTask.setErrorHandlingOption(IEngineTask.CANCEL_ON_ERROR);

        setReportParametersForTask(reportDesign, reportParams, renderTask);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        RenderOption renderOptions = getRenderOptions(outputFormat, reportParams);
        renderOptions.setOutputStream(out);
        renderTask.setRenderOption(renderOptions);

        setupDatasourcesForTask(datasources, renderTask);

        renderTask.run();
        evaluateErrorStatus(renderTask);

        byte[] buf = out.toByteArray();

        renderTask.close();

        return buf;
    }

    private void setupDatasourcesForTask(Set<DatasourceEntity> datasources, IRunAndRenderTask renderTask) {
        renderTask.getAppContext().put("OdaJDBCDriverClassPath", driverLocationProperties.getLocation());

        datasources.forEach(ds -> { // THE CODE BELOW works when you only have one datasource
            JDBCDatasourceEntity jdbcDatasource = (JDBCDatasourceEntity) ds;
            Connection conn = null;
            try {
                conn = DriverManager.getConnection(jdbcDatasource.getDbUrl(), jdbcDatasource.getUsername(), new String(jdbcDatasource.getPassword()));
                conn.isValid(5);
            } catch (SQLException e) {
                throw new GenerateReportExceptions.ReportDatasourceExceptionException(ds.getId().toString());
            }

            renderTask.getAppContext().put("OdaJDBCDriverPassInConnection", conn);
        });
    }

    // https://us.v-cdn.net/6030023/uploads/ipb/servlet.txt
    private Object convertParameterValue(int dataType, String paramValue) throws ParseException {
        Object value = null;

        switch (dataType) {
        case IScalarParameterDefn.TYPE_DECIMAL:
            value = new BigDecimal(paramValue);
            break;
        case IScalarParameterDefn.TYPE_FLOAT:
            value = Double.valueOf(paramValue);
            break;
        case IScalarParameterDefn.TYPE_INTEGER:
            value = Integer.valueOf(paramValue);
            break;
        case IScalarParameterDefn.TYPE_DATE_TIME:
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            value = sdf.parse(paramValue);
            break;
        case IScalarParameterDefn.TYPE_BOOLEAN:
            value = Boolean.valueOf(paramValue);
            break;
        case IScalarParameterDefn.TYPE_DATE:
            // https://www.eclipse.org/forums/index.php/t/318311/
            // http://developer.actuate.com/community/forum/index.php?/topic/19849-passing-date-parameter-to-birt-report/

            DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            java.util.Date date1 = format1.parse(paramValue);
            value = new java.sql.Date(date1.getTime());
            break;
        default:
            value = paramValue;
            break;
        }

        return value;
    }

    @SuppressWarnings("unchecked")
    private void setReportParametersForTask(IReportRunnable design, Map<String, String> reportParams, IRunAndRenderTask renderTask) throws EngineException {
        // Use this task to obtain information about parameters.
        IGetParameterDefinitionTask parameterDefTask = birtEngine.createGetParameterDefinitionTask(design);
        Collection<IParameterDefnBase> params = parameterDefTask.getParameterDefns(true);

        try {
            // https://books.google.co.za/books?id=fSAIBVaGV2wC&pg=PA279&lpg=PA279&dq=IGetParameterDefinitionTask+task+%3D+engin.createGetParameterDefinitionTask(design);&source=bl&ots=irpdx1B9gL&sig=5a-cIphugOSF1gWwDOtSk8uwoOc&hl=en&sa=X&ved=0ahUKEwj63t71st_QAhUJLsAKHbN0B_MQ6AEILzAD#v=onepage&q&f=false
            // https://www.eclipse.org/forums/index.php/t/238566/
            for (IParameterDefnBase paramDefBase : params) {
                // group section found
                if (paramDefBase instanceof IParameterGroupDefn) {
                    // TODO: handle grouped parameters
                } else {
                    // Parameters are not in a group
                    IScalarParameterDefn paramDef = (IScalarParameterDefn) paramDefBase;

                    Object reportParamSelectedValue = reportParams.get(paramDef.getName());
                    if (reportParamSelectedValue != null) {
                        renderTask.setParameterValue(paramDef.getName(), convertParameterValue(paramDef.getDataType(), reportParams.get(paramDef.getName())));
                    }
                }
            }
        } catch (ParseException e) {
            throw new EngineException(e.getMessage());
        }
    }

    private RenderOption getRenderOptions(String outputFormat, Map<String, String> reportParams) {

        RenderOption renderOptions = null;

        switch (outputFormat) {
        case ReportingConstants.REPORT_OUTPUTFORMAT_PDF:
            renderOptions = new PDFRenderOption();
            renderOptions.setOutputFormat(ReportingConstants.REPORT_OUTPUTFORMAT_PDF);
            break;
        case ReportingConstants.REPORT_OUTPUTFORMAT_XLSX:
            renderOptions = new EXCELRenderOption();
            renderOptions.setOutputFormat(ReportingConstants.REPORT_OUTPUTFORMAT_XLSX);
            break;
        case ReportingConstants.REPORT_OUTPUTFORMAT_HTML:
            renderOptions = new HTMLRenderOption();
            renderOptions.setOutputFormat(ReportingConstants.REPORT_OUTPUTFORMAT_HTML);

            ((HTMLRenderOption) renderOptions).setEmbeddable(true);
            break;
        case ReportingConstants.REPORT_OUTPUTFORMAT_CSV:
            CSVRenderOption csvOptions = new CSVRenderOption();
            csvOptions.setOutputFormat(CSVRenderOption.OUTPUT_FORMAT_CSV);

            csvOptions.setEmitterID(CSVRenderOption.OUTPUT_EMITTERID_CSV);
            csvOptions.setShowDatatypeInSecondRow(Boolean.valueOf(reportParams.get("showDatatypeInSecondRow")));
            csvOptions.setExportTableByName(reportParams.get("exportTableName"));
            csvOptions.setDelimiter(reportParams.get("delimiter"));
            csvOptions.setReplaceDelimiterInsideTextWith(reportParams.get("replaceDelimiterInsideTextWith"));

            renderOptions = csvOptions;
            break;
        default:
            throw new IllegalArgumentException("Invalid report format: " + outputFormat);
        }

        return renderOptions;
    }

    /**
     * Check for any errors that might have happened whilst generating the report.
     * 
     * @param task
     * @throws EngineException
     */
    @SuppressWarnings("unchecked")
    private void evaluateErrorStatus(IRunAndRenderTask task) throws EngineException {
        if (task.getStatus() == IEngineTask.STATUS_CANCELLED) {
            StringBuilder message = new StringBuilder();
            message.append("The error is: ");

            List<Throwable> errors = task.getErrors();
            if (!errors.isEmpty()) {
                for (Iterator<Throwable> iterator = errors.iterator(); iterator.hasNext();) {
                    Throwable throwable = iterator.next();
                    message.append(" " + throwable.getMessage());

                }
                throw new EngineException(message.toString(), errors.get(errors.size() - 1));
            }
            throw new EngineException(message.toString());
        }
    }

    public void setBirtReportEngine(IReportEngine birtReportEngine) {
        this.birtEngine = birtReportEngine;
    }
}
