/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.lookup.tag;

import org.openapitools.model.LookupTag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LookupTagService {

    public Page<LookupTag> findAllDistinctTags(Pageable pageable);

    public Page<LookupTag> filterTag(String tag, Pageable pageable);

}
