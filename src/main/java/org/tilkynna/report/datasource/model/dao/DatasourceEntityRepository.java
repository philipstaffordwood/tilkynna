/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.datasource.model.dao;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.tilkynna.report.datasource.model.db.DatasourceEntity;

@Repository
public interface DatasourceEntityRepository extends PagingAndSortingRepository<DatasourceEntity, UUID>, JpaSpecificationExecutor<DatasourceEntity> {

    public DatasourceEntity findByNameIgnoreCase(String name);

    public boolean existsByNameIgnoreCase(String name);

}
