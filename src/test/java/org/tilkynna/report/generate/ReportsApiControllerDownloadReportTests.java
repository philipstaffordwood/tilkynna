/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.generate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.openapitools.model.ExportFormat;
import org.openapitools.model.ReportStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.tilkynna.common.error.ResourceNotFoundExceptions;
import org.tilkynna.report.generate.download.DownloadService;
import org.tilkynna.report.generate.mockdata.GenerateReportMockDataGenerator;
import org.tilkynna.report.generate.model.db.ExportFormatEntity;
import org.tilkynna.report.generate.model.db.GeneratedReportEntity;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ReportsApiControllerDownloadReportTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    private DownloadService downloadService;

    @Test
    public void givenCorrelationId_whenReportFinished_thenStatus200AndResourceResponse() throws Exception {
        ClassPathResource resource = new ClassPathResource("example_pdf_report_file");
        GeneratedReportEntity generatedReport = GenerateReportMockDataGenerator.setupGeneratedReportEntity();
        UUID correlationId = generatedReport.getCorrelationId();

        Mockito.when(downloadService.findById(correlationId)).thenReturn(generatedReport);
        Mockito.when(downloadService.downloadReportOrGetStatus(generatedReport)).thenReturn(resource);
        Mockito.when(downloadService.getContentType(ExportFormat.PDF)).thenReturn("application/pdf");

        ExportFormatEntity exportFormatEntity = generatedReport.getExportFormat();
        String contentType = downloadService.getContentType(ExportFormat.fromValue(exportFormatEntity.getName()));
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(String.format("/reports/%s", correlationId))//
                .contentType(contentType)) //
                .andExpect(MockMvcResultMatchers.status().is(200)) //
                .andReturn();

        Assert.assertEquals(200, result.getResponse().getStatus());
        Assert.assertEquals(resource.contentLength(), result.getResponse().getContentAsByteArray().length);
        Assert.assertEquals(contentType, result.getResponse().getContentType());
    }

    @Test
    public void givenCorrelationId_whenReportNotFinished_thenStatusAccepted202() throws Exception {
        UUID correlationId = UUID.randomUUID();
        ReportStatus status = GenerateReportMockDataGenerator.setupReportStatus(correlationId);

        Mockito.doThrow(new GenerateReportExceptions.ReportNotReadyException(status)).when(downloadService).findById(correlationId);

        mockMvc.perform(get(String.format("/reports/%s", correlationId)) //
                .contentType(MediaType.APPLICATION_JSON)) //
                .andExpect(status().isAccepted()) //
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void givenCorrelationId_whenReportNotFound_thenStatusNotFound404() throws Exception {
        UUID randomCorrelationId = UUID.randomUUID();
        Mockito.doThrow(new ResourceNotFoundExceptions.GeneratedReportEntity(randomCorrelationId.toString())).when(downloadService).findById(randomCorrelationId);

        mockMvc.perform(get(String.format("/reports/%s", randomCorrelationId)) //
                .contentType(MediaType.APPLICATION_JSON)) //
                .andExpect(status().isNotFound()) //
                .andDo(MockMvcResultHandlers.print());
    }
}
