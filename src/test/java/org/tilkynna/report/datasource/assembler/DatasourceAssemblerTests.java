/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.datasource.assembler;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openapitools.model.DataSource;
import org.openapitools.model.DataSourceConnection;
import org.openapitools.model.DataSourceCreate;
import org.openapitools.model.DataSourceHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;
import org.tilkynna.report.datasource.mockdata.DatasouceMockDataGenerator;
import org.tilkynna.report.datasource.model.db.DatasourceEntity;
import org.tilkynna.report.datasource.model.db.JDBCDatasourceEntity;

@RunWith(SpringRunner.class)
public class DatasourceAssemblerTests {

    @Autowired
    private DatasourceAssembler datasourceAssembler;

    @TestConfiguration
    static class DatasourceAssemblerTestsContextConfiguration {

        @Bean
        public DatasourceAssembler datasourceAssembler() {
            return new DatasourceAssembler();
        }
    }

    @Test
    public void testMapDataSourceCreateToDatasourceEntity() throws Exception {
        DataSourceCreate src = DatasouceMockDataGenerator.setupDataSourceCreate();
        DatasourceEntity dest = datasourceAssembler.mapDataSourceCreateToDatasourceEntity(src);

        DataSourceConnection srcConnection = src.getConnection();
        JDBCDatasourceEntity destDatasource = (JDBCDatasourceEntity) dest;

        assertEquals("name not mapped correctly", src.getName(), dest.getName());
        assertEquals("description not mapped correctly", src.getDescription(), dest.getDescription());

        assertEquals("connection.driver not mapped correctly", srcConnection.getDriver(), destDatasource.getDriverClass());
        assertEquals("connection.url not mapped correctly", srcConnection.getUrl(), destDatasource.getDbUrl());
        assertEquals("connection.username not mapped correctly", srcConnection.getUsername(), destDatasource.getUsername());
        assertEquals("connection.password not mapped correctly", srcConnection.getPassword(), new String(destDatasource.getPassword()));
    }

    @Test
    public void testMapDatasourceEntityToDataSource() {
        JDBCDatasourceEntity src = DatasouceMockDataGenerator.setupJDBCDatasourceEntity("MyDataSource");
        DataSource dest = datasourceAssembler.mapDatasourceEntityToDataSource(src);

        DataSourceHeader destHeader = dest.getHeader();
        DataSourceConnection destConnection = dest.getConnection();

        assertEquals("name not mapped correctly", src.getName(), destHeader.getName());
        assertEquals("description not mapped correctly", src.getDescription(), destHeader.getDescription());

        assertEquals("connection.driver not mapped correctly", src.getDriverClass(), destConnection.getDriver());
        assertEquals("connection.url not mapped correctly", src.getDbUrl(), destConnection.getUrl());
        assertEquals("connection.username not mapped correctly", src.getUsername(), destConnection.getUsername());
        assertEquals("connection.password not mapped correctly", "************", destConnection.getPassword());
    }

    @Test
    public void testMapDatasourceEntity2DataSourceHeader() {
        JDBCDatasourceEntity src = DatasouceMockDataGenerator.setupJDBCDatasourceEntity("MyDataSource");
        DataSourceHeader dest = datasourceAssembler.mapDatasourceEntity2DataSourceHeader(src);

        assertEquals("id not mapped correctly", src.getId(), dest.getId());
        assertEquals("name not mapped correctly", src.getName(), dest.getName());
        assertEquals("description not mapped correctly", src.getDescription(), dest.getDescription());
        assertEquals("status not mapped correctly", src.isActive(), dest.getStatus());
    }

}
