/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.generate;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.openapitools.model.ReportStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.tilkynna.common.error.ResourceNotFoundExceptions;
import org.tilkynna.report.generate.download.DownloadService;
import org.tilkynna.report.generate.mockdata.GenerateReportMockDataGenerator;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ReportsApiControllerGetReportStatusTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    private DownloadService downloadService;

    @Test
    public void givenCorrelationId_whenGet_thenStatusOk200() throws Exception {
        UUID correlationId = UUID.randomUUID();
        ReportStatus reportStatus = GenerateReportMockDataGenerator.setupReportStatus(correlationId);

        Mockito.when(downloadService.getReportStatus(correlationId))
                .thenReturn(reportStatus);

        mockMvc.perform(get(String.format("/reports/%s/status", correlationId)) //
                .contentType(MediaType.APPLICATION_JSON)) //
                .andExpect(content().string(objectMapper.writeValueAsString(reportStatus))) //
                .andExpect(status().isOk()) //
                .andDo(MockMvcResultHandlers.print());

        verify(downloadService, times(1)).getReportStatus(correlationId);
        verifyNoMoreInteractions(downloadService);
    }

    @Test
    public void givenNonExistingCorrelationId_whenGet_thenNotFound() throws Exception {
        UUID randomCorrelationId = UUID.randomUUID();
        Mockito.doThrow(new ResourceNotFoundExceptions.GeneratedReportEntity(randomCorrelationId.toString()))
                .when(downloadService)
                .getReportStatus(randomCorrelationId);

        mockMvc.perform(get(String.format("/reports/%s/status", randomCorrelationId)) //
                .contentType(MediaType.APPLICATION_JSON)) //
                .andExpect(status().isNotFound()) //
                .andDo(MockMvcResultHandlers.print());
    }
}
