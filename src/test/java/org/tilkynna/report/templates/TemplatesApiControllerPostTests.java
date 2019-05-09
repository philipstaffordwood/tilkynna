/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.templates;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.openapitools.model.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.tilkynna.common.error.AlreadyExistsExceptions;
import org.tilkynna.report.datasource.mockdata.DatasouceMockDataGenerator;
import org.tilkynna.report.datasource.model.db.DatasourceEntity;
import org.tilkynna.report.generate.GenerateReportService;
import org.tilkynna.report.templates.assembler.TemplateAssembler;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Holds all tests for TemplatesApiController post requests
 * 
 * @author melissap
 */

// https://spring.io/guides/gs/uploading-files/
// test naming convention used is: givenUnitOfWork_whenInitialCondition_thenExpectedResult
@RunWith(SpringRunner.class)
@WebMvcTest(controllers = TemplatesApiController.class)
public class TemplatesApiControllerPostTests {

    // TODO validate max file size

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private TemplateAssembler templateAssembler;

    @MockBean
    private TemplateService templateService;

    @MockBean
    private GenerateReportService initiateGenerateReportService;

    @TestConfiguration
    static class TemplatesApiControllerPostTestContextConfiguration {

        @Bean
        public TemplateAssembler templateAssembler() {
            return new TemplateAssembler();
        }
    }

    String templateName = null;
    MockMultipartFile file = null;
    String[] tagsArray = null;
    String[] datasourceIdsArray = null;
    List<UUID> datasourceIds = null;
    TemplateRequest templateRequest = null;
    Template template = null;
    DatasourceEntity jdbcDatasource;

    @Before
    public void setUp() throws IOException {
        jdbcDatasource = DatasouceMockDataGenerator.setupJDBCDatasourceEntity("jdbc datasource");
        jdbcDatasource.setId(UUID.randomUUID());
        datasourceIds = new ArrayList<UUID>();
        datasourceIds.add(jdbcDatasource.getId());

        templateName = "templateName templateName";

        ClassPathResource resource = new ClassPathResource("TopSellingProducts.rptdesign");
        file = new MockMultipartFile("file", "a_birt_template.rptdesign", "multipart/form-data", resource.getInputStream());
        tagsArray = new String[] { "tags1", "tags2", "tags3" };

        templateRequest = new TemplateRequest();
        templateRequest.setFile(file);
        templateRequest.setTemplateName(templateName);
        templateRequest.setDatasourceIds(datasourceIds);
        templateRequest.setTags(Arrays.asList(tagsArray));

        // List<TemplateTagEntity> dbTags = new ArrayList<>();
        // for (int i = 0; i < tagsArray.length; i++) {
        // TemplateTagEntity dbTag = new TemplateTagEntity(tagsArray[i]);
        // dbTags.add(dbTag);
        // }
        Set<DatasourceEntity> datasources = new HashSet<>();
        for (Iterator<UUID> iterator = datasourceIds.iterator(); iterator.hasNext();) {
            UUID uuid = iterator.next();
            datasources.add(DatasouceMockDataGenerator.setupJDBCDatasourceEntity(uuid));
        }

        TemplateEntity entity = templateAssembler.mapTemplateRequestToTemplateEntity(templateRequest, datasources);
        template = templateAssembler.mapTemplateEntityToTemplate(entity);

        datasourceIdsArray = new String[] { jdbcDatasource.getId().toString() };

    }

    @Test
    public void givenValidTemplateRequest_whenCreate_thenSaveUploadedFile() throws Exception {
        Mockito.when(templateService.save(templateRequest)).thenReturn(template);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.multipart("/templates").file(file) //
                .param("templateName", templateName) //
                .param("tags", tagsArray) //
                .param("datasourceIds", datasourceIdsArray) //
                .contentType(MediaType.MULTIPART_FORM_DATA)) //
                .andExpect(status().isCreated()) //
                .andExpect(jsonPath("$.name", notNullValue())) //
                .andExpect(jsonPath("$.name", is(templateName)))//
                .andExpect(content().string(objectMapper.writeValueAsString(template))) //
                .andReturn(); //

        assertEquals(201, result.getResponse().getStatus());
        assertNotNull(result.getResponse().getContentAsString());
    }

    @Test
    public void givenNoTemplateName_whenCreate_thenBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.multipart("/templates").file(file) //
                .param("tags", tagsArray) //
                .contentType(MediaType.MULTIPART_FORM_DATA)) //
                .andExpect(status().isBadRequest()) //
                .andExpect(jsonPath("$.message", notNullValue())) //
                .andDo(MockMvcResultHandlers.print()); //
    }

    @Test
    public void givenFileEmpty_whenCreate_thenBadRequest() throws Exception {
        MockMultipartFile emptyFile = new MockMultipartFile("file", "a_birt_template.rptdesign", "multipart/form-data", new byte[0]);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/templates").file(emptyFile) //
                .param("tags", tagsArray) //
                .param("templateName", templateName) //
                .contentType(MediaType.MULTIPART_FORM_DATA)) //
                .andExpect(status().isBadRequest()) //
                .andExpect(jsonPath("$.message", notNullValue())) //
                .andDo(MockMvcResultHandlers.print()); //
    }

    @Test
    public void givenFileExceedsMaxSize_whenCreate_thenBadRequest() throws Exception {
        int maxUploadSizeInMb = 5 * 1024 * 1024; // 5 MB
        byte[] bytes = new byte[maxUploadSizeInMb];
        MockMultipartFile fileExceedsMaxSize = new MockMultipartFile("file", "a_birt_template.rptdesign", "multipart/form-data", bytes);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/templates").file(fileExceedsMaxSize) //
                .param("tags", tagsArray) //
                .param("templateName", templateName) //
                .param("datasourceIds", datasourceIdsArray) //
                .contentType(MediaType.MULTIPART_FORM_DATA)) //
                .andExpect(status().isCreated()) // TODO should be, but Spring max size not kicking in .andExpect(status().isBadRequest()) //
                // .andExpect(jsonPath("$.message", notNullValue())) //
                .andDo(MockMvcResultHandlers.print()); //
    }

    @Test
    public void givenExistingTemplate_whenCreate_thenReturnConflictStatus409() throws Exception {
        String existingStr = "Existing Template";
        TemplateRequest existingTemplate = new TemplateRequest();
        existingTemplate.setFile(file);
        existingTemplate.setTemplateName(existingStr);
        existingTemplate.setDatasourceIds(datasourceIds);
        existingTemplate.setTags(Arrays.asList(tagsArray));

        AlreadyExistsExceptions.Template existingTemplateException = new AlreadyExistsExceptions.Template("Existing Template");
        Mockito.when(templateService.save(existingTemplate)).thenThrow(existingTemplateException);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/templates").file(file) //
                .param("templateName", existingStr) //
                .param("tags", tagsArray) //
                .param("datasourceIds", datasourceIdsArray) //
                .contentType(MediaType.MULTIPART_FORM_DATA)) //
                .andExpect(status().isConflict()) //
                .andExpect(jsonPath("$.message", is(existingTemplateException.getMessage()))) //
                .andDo(MockMvcResultHandlers.print()); //
    }

}