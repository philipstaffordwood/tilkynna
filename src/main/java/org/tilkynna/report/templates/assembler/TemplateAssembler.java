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
import java.util.Set;
import java.util.UUID;

import org.openapitools.model.LookupTag;
import org.openapitools.model.Template;
import org.openapitools.model.TemplateTagList;
import org.openapitools.model.Templates;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.tilkynna.report.datasource.model.db.DatasourceEntity;
import org.tilkynna.report.templates.TemplateEntity;
import org.tilkynna.report.templates.TemplateRequest;
import org.tilkynna.report.templates.model.db.TemplateTagEntity;

@Component
public class TemplateAssembler {

    public Template mapTemplateEntityToTemplate(TemplateEntity src) {
        Template dest = new Template();
        dest.setName(src.getName());
        dest.setTemplateId(src.getId());

        // Map all associated Tags.
        List<TemplateTagEntity> templateEntityTags = src.getTemplateTags();
        if (templateEntityTags != null) {
            List<LookupTag> templateTags = new ArrayList<>();

            templateEntityTags.forEach(templateTagEntity -> { //
                LookupTag tag = new LookupTag();
                tag.setTag(templateTagEntity.getId().getTag());
                templateTags.add(tag);
            });
            dest.setTags(templateTags);
        }

        src.getDatasources().forEach(ds -> dest.addDatasourceIdsItem(ds.getId()));

        return dest;
    }

    public Templates mapListTemplateEntityToTemplates(List<TemplateEntity> src) {
        Templates dest = new Templates();

        List<Template> templates = new ArrayList<>();
        for (Iterator<TemplateEntity> iterator = src.iterator(); iterator.hasNext();) {
            TemplateEntity tempalteDB = iterator.next();

            Template template = this.mapTemplateEntityToTemplate(tempalteDB);
            templates.add(template);
        }

        dest.setTemplates(templates);

        return dest;
    }

    public TemplateEntity mapTemplateRequestToTemplateEntity(TemplateRequest src, Set<DatasourceEntity> dbDatasources) {
        MultipartFile file = src.getFile();

        TemplateEntity dest = new TemplateEntity(src.getTemplateName());
        dest.setOriginalFilename(StringUtils.cleanPath(file.getOriginalFilename()));
        dest.setDatasources(dbDatasources);
        // dest.addTags(src.getTags()); //DON'T set tags here as you need a template (with ID) before being able to have tags

        return dest;
    }

    public TemplateTagList mapListStringToTemplateTagList(UUID templateId, List<LookupTag> lookupTags) {
        TemplateTagList templateTagList = new TemplateTagList();
        templateTagList.setTemplateId(templateId);
        templateTagList.setTags(lookupTags);

        return templateTagList;
    }
}
