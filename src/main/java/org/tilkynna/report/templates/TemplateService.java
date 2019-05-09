/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.templates;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.openapitools.model.LookupTag;
import org.openapitools.model.Template;
import org.openapitools.model.TemplateDetail;
import org.openapitools.model.TemplateTagList;
import org.openapitools.model.Templates;
import org.tilkynna.report.datasource.model.db.DatasourceEntity;

//IMPLEMENTATION NEEDS TO SAVE TO DB & filestorage & any new tags
public interface TemplateService {

    public Template save(TemplateRequest templateRequest);

    public TemplateTagList addTemplateTags(UUID templateId, List<LookupTag> lookupTags);

    public void removeTempalteTags(UUID templateId);

    public Templates findAll();

    public Set<DatasourceEntity> gatherDatasourceEntities(List<UUID> datasourceIds);

    public TemplateDetail getTemplateDetail(UUID templateId);

}
