/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.datasource.model.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.tilkynna.report.datasource.mockdata.DatasouceMockDataGenerator;
import org.tilkynna.report.datasource.model.db.JDBCDatasourceEntity;

@RunWith(SpringRunner.class)
@DataJpaTest
public class DatasourceEntityRepositoryTests {

    @Autowired
    private DatasourceEntityRepository datasourceEntityRepository;

    @Test
    public void givenJdbcDatasource_whenSave_thenValid() throws Exception {
        String datasourceName = "name";

        JDBCDatasourceEntity jdbc = DatasouceMockDataGenerator.setupJDBCDatasourceEntity(datasourceName);

        datasourceEntityRepository.save(jdbc);

        JDBCDatasourceEntity jdbcFromDB = (JDBCDatasourceEntity) datasourceEntityRepository.findByNameIgnoreCase(datasourceName);

        assertNotNull("Failure - datasource should have been found", jdbcFromDB);
        assertEquals("Failure - datasource name are not equal", jdbcFromDB.getName(), jdbc.getName());
    }
}
