/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.datasource;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.openapitools.model.DataSource;
import org.openapitools.model.DataSourceCreate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.tilkynna.common.error.AlreadyExistsExceptions;
import org.tilkynna.report.datasource.mockdata.DatasouceMockDataGenerator;

import com.fasterxml.jackson.databind.ObjectMapper;

//test naming convention used is: givenUnitOfWork_whenInitialCondition_thenExpectedResult
@RunWith(SpringRunner.class)
@WebMvcTest(controllers = DatasourcesApiController.class)
public class DatasourceApiControllerPostTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    private DatasourceService datasourceService;

    @Test
    public void givenDataSourceCreate_whenCreate_thenReturnCreatedStatus201() throws Exception {
        DataSourceCreate input = DatasouceMockDataGenerator.setupDataSourceCreate();
        DataSource expectedResponse = DatasouceMockDataGenerator.setupDataSource();

        Mockito.when(datasourceService.save(input)).thenReturn(expectedResponse);

        mockMvc.perform(post("/datasources") //
                .contentType(MediaType.APPLICATION_JSON) //
                .content(objectMapper.writeValueAsString(input))) //
                .andExpect(status().isCreated()) //
                .andExpect(content().string(objectMapper.writeValueAsString(expectedResponse))) //
                .andDo(MockMvcResultHandlers.print()); //

        verify(datasourceService, times(1)).save(input);
        verifyNoMoreInteractions(datasourceService);
    }

    @Test
    public void givenExistingDataSource_whenCreate_thenReturnConflictStatus409() throws Exception {
        DataSourceCreate existingDatasource = DatasouceMockDataGenerator.setupDataSourceCreate();
        AlreadyExistsExceptions.Datasource existingDataSourceException = new AlreadyExistsExceptions.Datasource(existingDatasource.getName());
        Mockito.when(datasourceService.save(existingDatasource)).thenThrow(existingDataSourceException);

        mockMvc.perform(post("/datasources") //
                .contentType(MediaType.APPLICATION_JSON) //
                .content(objectMapper.writeValueAsString(existingDatasource))) //
                .andExpect(status().isConflict()) //
                .andExpect(jsonPath("$.message", is(existingDataSourceException.getMessage())))//
                .andDo(MockMvcResultHandlers.print()); //
    }

    @Test
    public void givenExistingDataSource_whenCreateDifferentStrCase_thenReturnConflictStatus409() throws Exception {
        DataSourceCreate upperDatasource = DatasouceMockDataGenerator.setupDataSourceCreate("UPPERDatasource");
        AlreadyExistsExceptions.Datasource existingDatasourceException = new AlreadyExistsExceptions.Datasource(upperDatasource.getName());

        DataSourceCreate lowerDatasource = DatasouceMockDataGenerator.setupDataSourceCreate("lower_datasource");

        Mockito.when(datasourceService.save(lowerDatasource)).thenThrow(existingDatasourceException);
        Mockito.when(datasourceService.save(upperDatasource)).thenThrow(existingDatasourceException);

        mockMvc.perform(post("/datasources") //
                .contentType(MediaType.APPLICATION_JSON) //
                .content(objectMapper.writeValueAsString(upperDatasource))) //
                .andExpect(status().isConflict()) //
                .andExpect(jsonPath("$.message", is(existingDatasourceException.getMessage())))//
                .andDo(MockMvcResultHandlers.print()); //
    }

    @Test
    public void givenDataSourceCreate_whenCreateFail_thenReturnInternalServerError500() throws Exception {
        DataSourceCreate datasourceCreate = DatasouceMockDataGenerator.setupDataSourceCreate("UPPERDatasource");
        Mockito.when(datasourceService.save(datasourceCreate)).thenThrow(new NullPointerException("NullPointerException message"));

        mockMvc.perform(post("/datasources") //
                .contentType(MediaType.APPLICATION_JSON) //
                .content(objectMapper.writeValueAsString(datasourceCreate))) //
                .andExpect(status().isInternalServerError()) //
                .andExpect(jsonPath("$.message", is(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()))) //
                .andDo(MockMvcResultHandlers.print()); //
    }

    @Test
    public void givenNullTag_whenCreate_thenReturnBadRequestStatus400() throws Exception {
        DataSourceCreate datasourceCreate = DatasouceMockDataGenerator.setupDataSourceCreate(null);

        mockMvc.perform(post("/datasources") //
                .contentType(MediaType.APPLICATION_JSON) //
                .content(objectMapper.writeValueAsString(datasourceCreate))) //
                .andExpect(status().isBadRequest()) //
                .andExpect(jsonPath("$.message", notNullValue())) //
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void givenEmptyTag_whenCreate_thenReturnBadRequestStatus400() throws Exception {
        DataSourceCreate datasourceCreate = DatasouceMockDataGenerator.setupDataSourceCreate("");

        mockMvc.perform(post("/datasources") //
                .contentType(MediaType.APPLICATION_JSON) //
                .content(objectMapper.writeValueAsString(datasourceCreate))) //
                .andExpect(status().isBadRequest()) //
                .andExpect(jsonPath("$.message", notNullValue()))//
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void givenDataSourceCreate_whenPutCreate_thenReturnMethodNotAllowedStatus405() throws Exception {
        DataSourceCreate datasourceCreate = DatasouceMockDataGenerator.setupDataSourceCreate("has text");

        mockMvc.perform(put("/datasources") //
                .contentType(MediaType.APPLICATION_JSON) //
                .content(objectMapper.writeValueAsString(datasourceCreate))) //
                .andExpect(status().isMethodNotAllowed()) //
                .andExpect(jsonPath("$.message", notNullValue())) //
                .andDo(MockMvcResultHandlers.print());
    }

    // TODO: 401 You are not authorized to access
    // TODO: 403 Forbidden
}
