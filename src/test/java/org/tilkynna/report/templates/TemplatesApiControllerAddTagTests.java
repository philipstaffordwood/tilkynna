/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.templates;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.openapitools.model.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.tilkynna.report.datasource.mockdata.DatasouceMockDataGenerator;
import org.tilkynna.report.datasource.model.db.DatasourceEntity;
import org.tilkynna.report.templates.assembler.TemplateAssembler;

//test naming convention used is: givenUnitOfWork_whenInitialCondition_thenExpectedResult
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TemplatesApiControllerAddTagTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TemplateAssembler templateAssembler;

    @MockBean
    private TemplateEntityRepository templateEntityRepository;

    TemplateEntity templateEntity = null;
    Template template = null;
    List<UUID> datasourceIds = null;
    DatasourceEntity jdbcDatasource;
    TemplateRequest templateRequest1;
    Set<DatasourceEntity> dbDatasources;

    @Before
    public void setUp() throws IOException {
        jdbcDatasource = DatasouceMockDataGenerator.setupJDBCDatasourceEntity(UUID.randomUUID());

        datasourceIds = new ArrayList<UUID>();
        datasourceIds.add(jdbcDatasource.getId());

        dbDatasources = new HashSet<>();
        dbDatasources.add(jdbcDatasource);

        ClassPathResource resource = new ClassPathResource("TopSellingProducts.rptdesign");
        MockMultipartFile file = new MockMultipartFile("file", "a_birt_template.rptdesign", "multipart/form-data", resource.getInputStream());
        String[] tagsArray = new String[] { "tags1", "tags2", "tags3" };

        String templateName = "Add Existing Template_" + UUID.randomUUID();
        templateRequest1 = new TemplateRequest();
        templateRequest1.setFile(file);
        templateRequest1.setTemplateName(templateName);
        templateRequest1.setDatasourceIds(datasourceIds);
        templateRequest1.setTags(Arrays.asList(tagsArray));

        templateEntity = templateAssembler.mapTemplateRequestToTemplateEntity(templateRequest1, dbDatasources);
        templateEntity.setId(UUID.randomUUID());
        template = templateAssembler.mapTemplateEntityToTemplate(templateEntity);
    }

    @Test
    public void givenNotExistingTemplateId_whenAddTag_thenNotFound() throws Exception {
        String addTagToTemplateStr = "addTagToTemplate1";
        UUID templateId = UUID.randomUUID();

        Mockito.when(templateEntityRepository.findById(templateId)).thenReturn(Optional.empty());

        mockMvc.perform(post("/templates/" + templateId.toString() + "/tags/" + addTagToTemplateStr) //
                .contentType(MediaType.APPLICATION_JSON)) //
                .andExpect(status().isNotFound()) //
                .andDo(MockMvcResultHandlers.print()); //
    }
}
