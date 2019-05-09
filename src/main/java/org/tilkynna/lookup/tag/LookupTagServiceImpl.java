/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.lookup.tag;

import org.openapitools.model.LookupTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class LookupTagServiceImpl implements LookupTagService {

    @Autowired
    private LookupTagRepository lookupTagRepository;

    @Autowired
    private TagsAssembler tagsAssembler;

    @Override
    public Page<LookupTag> findAllDistinctTags(Pageable pageable) {
        Page<String> tagsPage = lookupTagRepository.findAllDistinctTags(pageable);

        return tagsAssembler.mapStringPageToLookupTagPage(tagsPage);
    }

    @Override
    public Page<LookupTag> filterTag(String tag, Pageable pageable) {
        Page<String> tagsPage = lookupTagRepository.findDistinctTagsByKeyword(pageable, tag);

        return tagsAssembler.mapStringPageToLookupTagPage(tagsPage);
    }

}
