/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.lookup.tag;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.openapitools.model.LookupTag;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.tilkynna.report.templates.model.db.TemplateTagEntity;

@Component
public class TagsAssembler {

    public Page<LookupTag> mapStringPageToLookupTagPage(Page<String> tagsPage) {
        return tagsPage.map(tag -> mapTemplateTagEntityToLookupTag(tag));
    }

    public Page<LookupTag> mapLookupTagDaoToLookupTag(Pageable pageable, ArrayList<TemplateTagEntity> entities) {
        List<LookupTag> lookupTags = new ArrayList<LookupTag>();
        lookupTags.forEach(lookupTagElement -> { //
            LookupTag n = new LookupTag();
            n.setTag(lookupTagElement.getTag());
            lookupTags.add(n);
        });

        return new PageImpl<LookupTag>(lookupTags, pageable, entities.size());
    }

    public LookupTag mapTemplateTagEntityToLookupTag(String tag) {
        LookupTag lookupTag = new LookupTag();
        lookupTag.setTag(tag);

        return lookupTag;
    }

    Function<TemplateTagEntity, LookupTag> mapLookupTagEntityToLookupTag = new Function<TemplateTagEntity, LookupTag>() {
        @Override
        public LookupTag apply(TemplateTagEntity entity) {
            LookupTag dto = new LookupTag();
            // Conversion logic
            BeanUtils.copyProperties(entity, dto);

            return dto;
        }
    };

    public Page<LookupTag> mapLookupTagDaoToLookupTag(Page<TemplateTagEntity> entities) {
        return entities.map(entity -> mapLookupTagEntityToLookupTag.apply(entity));
    }

}
