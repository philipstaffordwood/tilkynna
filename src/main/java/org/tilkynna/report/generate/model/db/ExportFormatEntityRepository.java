/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.generate.model.db;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExportFormatEntityRepository extends PagingAndSortingRepository<ExportFormatEntity, UUID> {

    Optional<ExportFormatEntity> findByNameIgnoreCase(String string);

}
