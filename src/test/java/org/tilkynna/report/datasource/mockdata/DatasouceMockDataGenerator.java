/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.datasource.mockdata;

import java.util.UUID;

import org.openapitools.model.DataSource;
import org.openapitools.model.DataSourceConnection;
import org.openapitools.model.DataSourceCreate;
import org.openapitools.model.DataSourceHeader;
import org.tilkynna.report.datasource.model.db.JDBCDatasourceEntity;

public class DatasouceMockDataGenerator {
    private DatasouceMockDataGenerator() {

    }

    private final static String NAME = "Name For Datasource";
    private final static String DESCRIPTION = "Description For Datasource";
    private final static String USERNAME = "my_username";
    private final static String PASSWORD = "password";

    public static final DataSourceCreate setupDataSourceCreate() {
        return setupDataSourceCreate(NAME);
    }

    public static final DataSourceCreate setupDataSourceCreate(String name) {
        DataSourceCreate datasoureceCreate = new DataSourceCreate().name(name);
        datasoureceCreate.setDescription(DESCRIPTION);

        DataSourceConnection connection = setupDataSourceConnection();
        datasoureceCreate.setConnection(connection);

        return datasoureceCreate;
    }

    public static final DataSource setupDataSource() {
        DataSource datasoureceCreate = new DataSource();

        DataSourceHeader header = new DataSourceHeader();
        header.setId(UUID.randomUUID());
        header.setName(NAME);
        header.setDescription(DESCRIPTION);
        header.setStatus(true);

        DataSourceConnection connection = setupDataSourceConnection();

        datasoureceCreate.setHeader(header);
        datasoureceCreate.setConnection(connection);

        return datasoureceCreate;
    }

    public static final JDBCDatasourceEntity setupJDBCDatasourceEntity(UUID uuid) {
        JDBCDatasourceEntity jdbc = setupJDBCDatasourceEntity(uuid.toString());
        jdbc.setId(uuid);

        return jdbc;
    }

    public static final JDBCDatasourceEntity setupJDBCDatasourceEntity(String DATASOURCE_NAME) {
        JDBCDatasourceEntity jdbc = new JDBCDatasourceEntity();
        jdbc.setActive(true);
        jdbc.setName(DATASOURCE_NAME);
        jdbc.setDriverClass("org.postgresql.Driver");
        jdbc.setDbUrl("jdbc:postgresql://localhost:5432/my_report_data_db");
        jdbc.setUsername("username");
        jdbc.setPassword("password".getBytes());

        return jdbc;
    }

    private static DataSourceConnection setupDataSourceConnection() {
        DataSourceConnection connection = new DataSourceConnection();
        connection.setDriver("org.postgresql.Driver");
        connection.setUrl("jdbc:postgresql://localhost:5432/my_report_data_db");
        connection.setUsername(USERNAME);
        connection.setPassword(PASSWORD);
        return connection;
    }

}
