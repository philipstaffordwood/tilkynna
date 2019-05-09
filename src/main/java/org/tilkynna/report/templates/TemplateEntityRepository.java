/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.templates;

import java.util.UUID;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemplateEntityRepository extends PagingAndSortingRepository<TemplateEntity, UUID> {
    public TemplateEntity findByNameIgnoreCase(String name);

    public boolean existsByNameIgnoreCase(String name);

}
