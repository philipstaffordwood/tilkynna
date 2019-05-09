/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.datasource;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.tilkynna.common.error.ResourceNotFoundExceptions;
import org.tilkynna.report.datasource.model.dao.DatasourceEntityRepository;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = DatasourcesApiController.class)
public class DatasourceApiControllerInactivateTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DatasourceEntityRepository datasourceEntityRepository;

    @MockBean
    private DatasourceService datasourceService;

    @Test
    public void givenNotExistingDatasourceId_whenInvalidate_thenNotFound() throws Exception {
        UUID randomUUID = UUID.randomUUID();
        Mockito.doThrow(new ResourceNotFoundExceptions.Datasource(randomUUID.toString())).when(datasourceService).inactivateDataSource(randomUUID);

        mockMvc.perform(put(String.format("/datasources/%s/inactivate", randomUUID)) //
                .contentType(MediaType.APPLICATION_JSON)) //
                .andExpect(status().isNotFound()) //
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void givenExistingDatasourceId_whenInvalidate_thenOk() throws Exception {
        UUID randomUUID = UUID.randomUUID();
        Mockito.doNothing().when(datasourceService).inactivateDataSource(randomUUID);

        mockMvc.perform(put(String.format("/datasources/%s/inactivate", randomUUID)) //
                .contentType(MediaType.APPLICATION_JSON)) //
                .andExpect(status().isOk()) //
                .andDo(MockMvcResultHandlers.print());
    }
}
