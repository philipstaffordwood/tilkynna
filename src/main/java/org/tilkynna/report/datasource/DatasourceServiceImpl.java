/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.datasource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.openapitools.model.DataSource;
import org.openapitools.model.DataSourceConnection;
import org.openapitools.model.DataSourceCreate;
import org.openapitools.model.DataSourceHeader;
import org.openapitools.model.LookupConnectionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.tilkynna.common.error.AlreadyExistsExceptions;
import org.tilkynna.common.error.ResourceNotFoundExceptions;
import org.tilkynna.report.datasource.assembler.DatasourceAssembler;
import org.tilkynna.report.datasource.model.dao.DatasourceEntityRepository;
import org.tilkynna.report.datasource.model.db.DataSourceTypes;
import org.tilkynna.report.datasource.model.db.DatasourceEntity;
import org.tilkynna.report.datasource.model.db.JDBCDatasourceEntity;

@Service
public class DatasourceServiceImpl implements DatasourceService {

    @Autowired
    private DatasourceAssembler datasourceAssembler;

    @Autowired
    private DatasourceEntityRepository datasourceEntityRepository;

    @Override
    public DataSource save(DataSourceCreate datasourceCreate) {
        if (datasourceEntityRepository.existsByNameIgnoreCase(datasourceCreate.getName())) {
            throw new AlreadyExistsExceptions.Datasource(datasourceCreate.getName());
        }

        DatasourceEntity dsEntity = datasourceAssembler.mapDataSourceCreateToDatasourceEntity(datasourceCreate);
        switch (dsEntity.getType()) {
        case DataSourceTypes.Values.JDBC:
            JDBCDatasourceEntity jdbc = (JDBCDatasourceEntity) dsEntity;
            jdbc.setActive(testJdbcConnection(jdbc.getDbUrl(), jdbc.getUsername(), jdbc.getPassword()));
            break;
        case DataSourceTypes.Values.FLAT_FILE:
            break;
        default:
            break;
        }

        datasourceEntityRepository.save(dsEntity);
        return datasourceAssembler.mapDatasourceEntityToDataSource(dsEntity);
    }

    // TODO is there a way to dynamically add drivers to DriverManager such that we can use the DriversLocationProperties.location property to find driders
    // export JDBC_DRIVERS_PATH=drivers
    // java -Dloader.path=$JDBC_DRIVERS_PATH -jar target/tilkynna.jar
    private Boolean testJdbcConnection(String url, String username, byte[] password) {
        try (Connection conn = DriverManager.getConnection(url, username, new String(password));) {
            return Boolean.TRUE;
        } catch (SQLException e) {
            // ignore exception as we validating connection works or not, if there is an exception respond to user with failure, this is known/wanted feature
            return Boolean.FALSE;
        }
    }

    @Override
    public Page<DataSourceHeader> findAll(Specification<DatasourceEntity> spec, Pageable pageable) {
        Page<DatasourceEntity> entities = datasourceEntityRepository.findAll(spec, pageable);

        return datasourceAssembler.mapPageDatasourceEntity2PageDataSourceHeader(entities);
    }

    @Override
    public DataSource getDataSource(UUID datasourceId) {
        Optional<DatasourceEntity> ds = datasourceEntityRepository.findById(datasourceId);

        if (ds.isPresent()) {
            DatasourceEntity dsEntity = ds.get();

            return datasourceAssembler.mapDatasourceEntityToDataSource(dsEntity);
        } else {
            throw new ResourceNotFoundExceptions.Datasource(datasourceId.toString());
        }
    }

    @Override
    public Boolean validateConnection(DataSourceConnection connection) {
        return testJdbcConnection(connection.getUrl(), connection.getUsername(), connection.getPassword().getBytes());
    }

    @Override
    public Boolean validateConnection(UUID datasourceId) {
        Boolean returnValue = Boolean.FALSE;
        Optional<DatasourceEntity> optionalDs = datasourceEntityRepository.findById(datasourceId);

        if (optionalDs.isPresent()) {
            DatasourceEntity datasourceDB = optionalDs.get();

            switch (datasourceDB.getType()) {
            case DataSourceTypes.Values.JDBC:
                JDBCDatasourceEntity jdbc = (JDBCDatasourceEntity) datasourceDB;
                returnValue = testJdbcConnection(jdbc.getDbUrl(), jdbc.getUsername(), jdbc.getPassword());
                break;
            case DataSourceTypes.Values.FLAT_FILE:
                returnValue = Boolean.FALSE;
                break;
            default:
                returnValue = Boolean.FALSE;
            }

            datasourceDB.setActive(returnValue);
            datasourceEntityRepository.save(datasourceDB);
        }

        return returnValue;
    }

    @Override
    public void inactivateDataSource(UUID datasourceId) {
        Optional<DatasourceEntity> optionalDs = datasourceEntityRepository.findById(datasourceId);

        if (optionalDs.isPresent()) {
            DatasourceEntity datasourceDB = optionalDs.get();
            datasourceDB.setActive(false);
            datasourceEntityRepository.save(datasourceDB);
        } else {
            throw new ResourceNotFoundExceptions.Datasource(datasourceId.toString());
        }
    }

    @Override
    public List<LookupConnectionType> getConnectionTypes() {
        DataSourceTypes[] types = DataSourceTypes.values();
        types = Arrays.stream(types).filter(x -> x.isImplemented()).toArray(DataSourceTypes[]::new);

        List<LookupConnectionType> connectionTypes = new ArrayList();
        for (int i = 0; i < types.length; i++) {
            LookupConnectionType connectionType = new LookupConnectionType().connectionTypeName(types[i].name());
            connectionType.setDescription(types[i].getDescription());

            connectionTypes.add(connectionType);
        }

        return connectionTypes;
    }
}
