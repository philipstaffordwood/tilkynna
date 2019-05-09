/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.datasource;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openapitools.model.DataSourceHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.tilkynna.common.utils.PaginatedResultsRetrievedEventListener;
import org.tilkynna.report.datasource.assembler.DatasourceAssembler;
import org.tilkynna.report.datasource.mockdata.DatasouceMockDataGenerator;
import org.tilkynna.report.datasource.model.dao.DatasourceEntityRepository;
import org.tilkynna.report.datasource.model.db.DatasourceEntity;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase(replace = Replace.NONE) // needing this so that initial db from Flyway does not get dropped
@AutoConfigureMockMvc
public class DatasourceApiControllerFilterTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private DatasourceAssembler datasourceAssembler;

    @Autowired
    private DatasourceEntityRepository datasourceEntityRepository;

    private DatasourceEntity liabilitiesDatasource;
    private DatasourceEntity assetsDatasource;

    private DatasourceEntity activeDatasource;
    private DatasourceEntity inActiveDatasource;

    @Before
    public void init() {
        liabilitiesDatasource = DatasouceMockDataGenerator.setupJDBCDatasourceEntity("liabilities");
        datasourceEntityRepository.save(liabilitiesDatasource);

        assetsDatasource = DatasouceMockDataGenerator.setupJDBCDatasourceEntity("assets");
        datasourceEntityRepository.save(assetsDatasource);

        activeDatasource = DatasouceMockDataGenerator.setupJDBCDatasourceEntity("activeDatasource");
        activeDatasource.setActive(Boolean.TRUE);
        datasourceEntityRepository.save(activeDatasource);

        inActiveDatasource = DatasouceMockDataGenerator.setupJDBCDatasourceEntity("inActiveDatasource");
        inActiveDatasource.setActive(Boolean.FALSE);
        datasourceEntityRepository.save(inActiveDatasource);
    }

    @After
    public void end() {
        datasourceEntityRepository.deleteAll();
    }

    @Test
    public void givenLookupTags_whenFilterName_thenOnlyTagsContainingC() throws Exception {
        DataSourceHeader liabilitiesDataSourceHeader = datasourceAssembler.mapDatasourceEntity2DataSourceHeader(liabilitiesDatasource);
        DataSourceHeader[] expectedResponse = new DataSourceHeader[] { liabilitiesDataSourceHeader };

        mockMvc.perform(get("/datasources?filterName=liabilities").contentType(MediaType.APPLICATION_JSON)) //
                .andExpect(status().isOk()) //
                .andExpect(header().string(PaginatedResultsRetrievedEventListener.PAGINATION_HEADER_NAME, is(notNullValue())))//
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8)) //
                .andExpect(jsonPath("$", hasSize(1)))//
                .andExpect(content().string(objectMapper.writeValueAsString(expectedResponse))) //
                .andDo(MockMvcResultHandlers.print());//
    }

    @Test
    public void givenLookupTags_whenFilterNameMixedCase_thenOnlyTagsContainingC() throws Exception {
        DataSourceHeader liabilitiesDataSourceHeader = datasourceAssembler.mapDatasourceEntity2DataSourceHeader(liabilitiesDatasource);
        DataSourceHeader[] expectedResponse = new DataSourceHeader[] { liabilitiesDataSourceHeader };

        mockMvc.perform(get("/datasources?filterName=liAbIliTies").contentType(MediaType.APPLICATION_JSON)) //
                .andExpect(status().isOk()) //
                .andExpect(header().string(PaginatedResultsRetrievedEventListener.PAGINATION_HEADER_NAME, is(notNullValue())))//
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8)) //
                .andExpect(jsonPath("$", hasSize(1)))//
                .andExpect(content().string(objectMapper.writeValueAsString(expectedResponse))) //
                .andDo(MockMvcResultHandlers.print());//
    }

}
