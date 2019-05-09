/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.templates.assembler;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.tilkynna.report.datasource.mockdata.DatasouceMockDataGenerator;
import org.tilkynna.report.datasource.model.db.DatasourceEntity;
import org.tilkynna.report.templates.TemplateEntity;
import org.tilkynna.report.templates.TemplateRequest;

@RunWith(SpringRunner.class)
public class TemplateAssemblerTests {

    @Autowired
    private TemplateAssembler templateAssembler;

    @TestConfiguration
    static class TemplateAssemblerTestsContextConfiguration {

        @Bean
        public TemplateAssembler templateAssembler() {
            return new TemplateAssembler();
        }
    }

    @Test
    public void mapTemplateRequestToTemplateEntity() throws Exception {
        ClassPathResource resource = new ClassPathResource("TopSellingProducts.rptdesign");
        MockMultipartFile file = new MockMultipartFile("file", "a_birt_template.rptdesign", "multipart/form-data", resource.getInputStream());
        String[] tagsArray = new String[] { "tags1", "tags2", "tags3" };
        List<UUID> datasourceIds = new ArrayList<UUID>();
        datasourceIds.add(UUID.randomUUID());
        //
        TemplateRequest src = new TemplateRequest();
        src.setFile(file);
        src.setTemplateName("templateName");
        src.setTags(Arrays.asList(tagsArray));
        src.setDatasourceIds(datasourceIds);

        // get db objects for datasources
        Set<DatasourceEntity> datasources = new HashSet<>();
        for (Iterator<UUID> iterator = datasourceIds.iterator(); iterator.hasNext();) {
            UUID uuid = iterator.next();
            datasources.add(DatasouceMockDataGenerator.setupJDBCDatasourceEntity(uuid));
        }
        //
        // convert
        TemplateEntity dest = templateAssembler.mapTemplateRequestToTemplateEntity(src, datasources);
        //
        // assert tests
        assertEquals("templateName not mapped correctly", src.getTemplateName(), dest.getName());
        assertEquals("originalFilename not mapped correctly", file.getOriginalFilename(), dest.getOriginalFilename());

        //
        List<UUID> uuidResponse = new ArrayList<>();
        dest.getDatasources().forEach(dsDb -> uuidResponse.add(dsDb.getId()));
        assertEquals("datasources not mapped correctly", datasourceIds, uuidResponse);
        //
    }
}
