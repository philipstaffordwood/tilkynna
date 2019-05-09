/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.engine;

import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.birt.report.engine.api.EngineException;
import org.tilkynna.engine.model.TemplateEngineParameter;
import org.tilkynna.report.datasource.model.db.DatasourceEntity;

public interface TemplateEngine {
    public byte[] generateReport(String outputFormat, Map<String, String> reportParams, String reportFileName, Set<DatasourceEntity> datasources) throws ParseException, EngineException;

    public List<TemplateEngineParameter> getTemplateParameters(String templateId, String reportFile);

}
