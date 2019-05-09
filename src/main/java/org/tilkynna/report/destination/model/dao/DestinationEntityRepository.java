/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.destination.model.dao;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.tilkynna.report.destination.model.db.DestinationEntity;

@Repository
public interface DestinationEntityRepository extends PagingAndSortingRepository<DestinationEntity, UUID>, JpaSpecificationExecutor<DestinationEntity> {

    public DestinationEntity findByNameIgnoreCase(String name);

    public boolean existsByNameIgnoreCase(String name);

}
