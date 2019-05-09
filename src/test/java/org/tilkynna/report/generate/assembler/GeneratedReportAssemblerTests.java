/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.generate.assembler;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openapitools.model.ReportStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;
import org.tilkynna.report.generate.mockdata.GenerateReportMockDataGenerator;
import org.tilkynna.report.generate.model.db.GeneratedReportEntity;

@RunWith(SpringRunner.class)
public class GeneratedReportAssemblerTests {

    @Autowired
    private GeneratedReportAssembler generatedReportAssembler;

    @TestConfiguration
    static class GeneratedReportAssemblerTestsConfiguration {

        @Bean
        public GeneratedReportAssembler generatedReportAssembler() {
            return new GeneratedReportAssembler();
        }
    }

    @Test
    public void testMapGeneratedReportEntityToReportStatus() throws Exception {
        GeneratedReportEntity src = GenerateReportMockDataGenerator.setupGeneratedReportEntity();
        ReportStatus dest = generatedReportAssembler.mapGeneratedReportEntityToReportStatus(src);

        assertEquals("correlationId not mapped correctly", src.getCorrelationId(), dest.getCorrelationId());
        assertEquals("reportStatus not mapped correctly", src.getReportStatus()
                .name(),
                dest.getStatus()
                        .name());
        assertThat("url should have correlationId", dest.getUrl(), containsString(src.getCorrelationId()
                .toString()));

    }
}
