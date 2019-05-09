/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.templates;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.tilkynna.report.datasource.model.db.JDBCDatasourceEntity;

@RunWith(SpringRunner.class)
@DataJpaTest
public class TemplateEntityRepositoryTests {

    @Autowired
    private TemplateEntityRepository templateRepository;

    @Test
    public void givenTemplateName_whenFindByTemplateNameIgnoreCase_thenValid() throws Exception {
        List<String> tags = new ArrayList<>();

        TemplateEntity template = new TemplateEntity("template1");
        template.setOriginalFilename("templateFile");
        template.addTags(tags);

        templateRepository.save(template);

        TemplateEntity templateFromDB = templateRepository.findByNameIgnoreCase("TemPlate1");

        assertNotNull("Failure - Template should have been found", templateFromDB);
        assertEquals("Failure - TemplateNames are not equal", templateFromDB.getName(), template.getName());
    }

    // =====================================
    // Template >-< Datasource Tests
    // =====================================
    @Test
    public void givenTemplate_whenAddDatasource_thenValid() throws Exception {
        String DATASOURCE_NAME = "name";
        List<String> tags = new ArrayList<>();

        TemplateEntity template = new TemplateEntity("template1");
        template.setOriginalFilename("templateFile");
        template.addTags(tags);

        JDBCDatasourceEntity jdbc = new JDBCDatasourceEntity();
        jdbc.setActive(true);
        jdbc.setUsername("username for JDBC");
        jdbc.setName(DATASOURCE_NAME);
        jdbc.setDriverClass("driverClass");
        jdbc.setDbUrl("dbUrl");
        jdbc.setPassword("password".getBytes());

        template.addDatasource(jdbc);

        templateRepository.save(template);

        TemplateEntity templateFromDB = templateRepository.findByNameIgnoreCase("TemPlate1");

        assertNotNull("Failure - Template should have been found", templateFromDB);
        assertEquals("Failure - TemplateNames are not equal", templateFromDB.getName(), template.getName());
    }

}
