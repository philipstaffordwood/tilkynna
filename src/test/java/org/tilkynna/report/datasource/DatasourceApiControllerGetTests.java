/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.datasource;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.tilkynna.report.datasource.mockdata.DatasouceMockDataGenerator;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class DatasourceApiControllerGetTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    private DatasourceService datasourceService;

    @Test
    public void givenLookupTags_whenGet_thenStatusOk200() throws Exception {
        UUID datasourceId = UUID.randomUUID();
        Mockito.when(datasourceService.getDataSource(datasourceId)).thenReturn(DatasouceMockDataGenerator.setupDataSource());

        mockMvc.perform(get(String.format("/datasources/%s", datasourceId)) //
                .contentType(MediaType.APPLICATION_JSON)) //
                .andExpect(status().isOk()) //
                .andDo(MockMvcResultHandlers.print());

        verify(datasourceService, times(1)).getDataSource(datasourceId);
        verifyNoMoreInteractions(datasourceService);
    }
}
