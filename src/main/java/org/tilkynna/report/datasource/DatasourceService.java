/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.datasource;

import java.util.List;
import java.util.UUID;

import org.openapitools.model.DataSource;
import org.openapitools.model.DataSourceConnection;
import org.openapitools.model.DataSourceCreate;
import org.openapitools.model.DataSourceHeader;
import org.openapitools.model.LookupConnectionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.tilkynna.report.datasource.model.db.DatasourceEntity;

public interface DatasourceService {

    public List<LookupConnectionType> getConnectionTypes();

    public DataSource save(DataSourceCreate templateRequest);

    public Boolean validateConnection(DataSourceConnection dataSourceConnection);

    /**
     * Test whether the destination is reachable from the report server. <br>
     * Sets the status to 'active' when the destination can be reached. <br>
     * Otherwise the status is 'inactive'. <br>
     * 
     * @param datasourceId
     * @return
     */
    public Boolean validateConnection(UUID datasourceId);

    public void inactivateDataSource(UUID datasourceId);

    public Page<DataSourceHeader> findAll(Specification<DatasourceEntity> spec, Pageable pageable);

    public DataSource getDataSource(UUID datasourceId);
}
