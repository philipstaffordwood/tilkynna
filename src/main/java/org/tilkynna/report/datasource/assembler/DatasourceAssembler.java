/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.datasource.assembler;

import java.util.function.Function;

import org.openapitools.model.DataSource;
import org.openapitools.model.DataSourceConnection;
import org.openapitools.model.DataSourceCreate;
import org.openapitools.model.DataSourceHeader;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.tilkynna.report.datasource.model.db.DataSourceTypes;
import org.tilkynna.report.datasource.model.db.DatasourceEntity;
import org.tilkynna.report.datasource.model.db.FlatFileDatasourceEntity;
import org.tilkynna.report.datasource.model.db.JDBCDatasourceEntity;

@Component
public class DatasourceAssembler {

    private DatasourceEntity toDatasourceEntity(String type, DataSourceCreate dataSourceCreate) {
        DataSourceConnection createConnection = dataSourceCreate.getConnection();

        switch (type) {
        case DataSourceTypes.Values.JDBC:
            JDBCDatasourceEntity jdbc = new JDBCDatasourceEntity();
            jdbc.setName(dataSourceCreate.getName());
            jdbc.setDescription(dataSourceCreate.getDescription());

            jdbc.setDriverClass(createConnection.getDriver());
            jdbc.setUsername(createConnection.getUsername());
            jdbc.setPassword(createConnection.getPassword().getBytes());
            jdbc.setDbUrl(createConnection.getUrl());
            jdbc.setActive(true); // by default Datasource is active

            return jdbc;
        case DataSourceTypes.Values.FLAT_FILE:
            return new FlatFileDatasourceEntity();
        default:
            throw new DatasoureTypeNotSupported(type);
        }

    }

    public DatasourceEntity mapDataSourceCreateToDatasourceEntity(DataSourceCreate dataSourceCreate) {

        return toDatasourceEntity(DataSourceTypes.Values.JDBC, dataSourceCreate);
    }

    public DataSource mapDatasourceEntityToDataSource(DatasourceEntity datasourceEntity) {
        DataSource ds = new DataSource();

        DataSourceHeader header = new DataSourceHeader();
        header.setId(datasourceEntity.getId());
        header.setDescription(datasourceEntity.getDescription());
        header.setName(datasourceEntity.getName());
        header.setStatus(datasourceEntity.isActive());

        JDBCDatasourceEntity jdbc = (JDBCDatasourceEntity) datasourceEntity;
        DataSourceConnection connection = new DataSourceConnection();
        connection.setUrl(jdbc.getDbUrl());
        connection.setUsername(jdbc.getUsername());
        connection.setPassword("************"); // always mask out password sent back to API response
        connection.setDriver(jdbc.getDriverClass());

        ds.setHeader(header);
        ds.setConnection(connection);

        return ds;
    }

    public DataSourceHeader mapDatasourceEntity2DataSourceHeader(DatasourceEntity entity) {
        DataSourceHeader dto = new DataSourceHeader();
        // Conversion logic
        BeanUtils.copyProperties(entity, dto);
        dto.setStatus(entity.isActive());

        return dto;

    }

    public Page<DataSourceHeader> mapPageDatasourceEntity2PageDataSourceHeader(Page<DatasourceEntity> entities) {
        Function<DatasourceEntity, DataSourceHeader> mapper = (DatasourceEntity entity) -> {
            return mapDatasourceEntity2DataSourceHeader(entity);
        };

        return entities.map(entity -> mapper.apply(entity));
    }
}
