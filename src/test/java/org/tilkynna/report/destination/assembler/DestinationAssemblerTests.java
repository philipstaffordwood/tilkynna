/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.destination.assembler;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openapitools.model.DestinationResponseBase;
import org.openapitools.model.DestinationResponseHeader;
import org.openapitools.model.DestinationResponseHeader.StatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;
import org.tilkynna.report.destination.mockdata.SFTPDestinationMockDataGenerator;
import org.tilkynna.report.destination.mockdata.SMTPDestinationMockDataGenerator;
import org.tilkynna.report.destination.model.db.DestinationEntity;
import org.tilkynna.report.destination.model.db.SFTPDestinationEntity;
import org.tilkynna.report.destination.model.db.SMTPDestinationEntity;

@RunWith(SpringRunner.class)
public class DestinationAssemblerTests {

    @Autowired
    private DestinationAssembler destinationAssembler;

    @TestConfiguration
    static class DestinationAssemblerTestsContextConfiguration {

        @Bean
        public DestinationAssembler destinationAssembler() {
            return new DestinationAssembler();
        }
    }

    @Test
    public void testMapDestinationEntityToDestinationResponseBase() throws Exception {
        UUID destinationId = UUID.randomUUID();
        SFTPDestinationEntity src = SFTPDestinationMockDataGenerator.setupSFTPDestinationEntity(destinationId, "MyTest_SFTP");

        DestinationResponseBase dest = destinationAssembler.mapDestinationEntityToDestinationResponseBase(src);
        DestinationResponseHeader header = dest.getHeader();

        assertEquals("name not mapped correctly", src.getName(), header.getName());
        assertEquals("description not mapped correctly", src.getDescription(), header.getDescription());
        assertEquals("downloadable not mapped correctly", src.isDownloadable(), header.getDownloadable());
        assertEquals("type not mapped correctly", src.getType(), dest.getDestinationType().name());
    }

    @Test
    public void testMapDestinationEntityToDestinationResponseHeader() {
        UUID destinationId = UUID.randomUUID();
        SFTPDestinationEntity src = SFTPDestinationMockDataGenerator.setupSFTPDestinationEntity(destinationId, "MyTest_SFTP");

        DestinationResponseHeader dest = destinationAssembler.mapDestinationEntityToDestinationResponseHeader(src);

        assertEquals("name not mapped correctly", src.getName(), dest.getName());
        assertEquals("description not mapped correctly", src.getDescription(), dest.getDescription());
        assertEquals("downloadable not mapped correctly", src.isDownloadable(), dest.getDownloadable());
        assertEquals("status not mapped correctly", StatusEnum.ACTIVE, dest.getStatus());
    }

    @Test
    public void testMapListDestinationEntityToDestinationResponseHeader() {
        SFTPDestinationEntity sftp = SFTPDestinationMockDataGenerator.setupSFTPDestinationEntity(UUID.randomUUID(), "MyTest_SMTP");
        SMTPDestinationEntity smtp = SMTPDestinationMockDataGenerator.setupSMTPDestinationEntity("SMPT_UNIT_TEST");

        List<DestinationEntity> src = new ArrayList<DestinationEntity>();
        src.add(sftp);
        src.add(smtp);

        List<DestinationResponseHeader> dest = destinationAssembler.mapListDestinationEntityToDestinationResponseHeader(src);

        assertEquals("size not mapped correctly", src.size(), dest.size());
    }

}
