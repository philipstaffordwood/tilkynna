/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.destination.model.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.tilkynna.report.destination.mockdata.SFTPDestinationMockDataGenerator;
import org.tilkynna.report.destination.mockdata.SMTPDestinationMockDataGenerator;
import org.tilkynna.report.destination.model.db.SFTPDestinationEntity;
import org.tilkynna.report.destination.model.db.SMTPDestinationEntity;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class DestinationEntityRepositoryTests {

    @Autowired
    private DestinationEntityRepository destinationEntityRepository;

    @Test
    public void givenSFTPDestination_whenSave_thenValid() throws Exception {
        String destinationName = "STREAM_UNIT_TEST";

        SFTPDestinationEntity sftp = SFTPDestinationMockDataGenerator.setupSFTPDestinationEntity(destinationName);
        destinationEntityRepository.save(sftp);

        SFTPDestinationEntity sftpFromDB = (SFTPDestinationEntity) destinationEntityRepository.findByNameIgnoreCase(destinationName);

        assertNotNull("Failure - destination should have been found", sftpFromDB);
        assertEquals("Failure - destination name are not equal", sftpFromDB.getName(), sftp.getName());
        assertNotNull("Failure - destinationParameters for SFTP should not be null", sftp.getDestinationParameters());
        assertEquals("Failure - destinationParameters for SFTP should have 1", 1, sftp.getDestinationParameters().size());
        // assertEquals("Failure - destination password are not equal",
        // sftpFromDB.getPassword(), new String(sftp.getPassword()));
    }

    @Test
    public void givenSMPTDestination_whenSave_thenValid() throws Exception {
        String destinationName = "SMPT_UNIT_TEST";

        SMTPDestinationEntity smtp = SMTPDestinationMockDataGenerator.setupSMTPDestinationEntity(destinationName);
        destinationEntityRepository.save(smtp);

        SMTPDestinationEntity smtpFromDB = (SMTPDestinationEntity) destinationEntityRepository.findByNameIgnoreCase(destinationName);

        assertNotNull("Failure - destination should have been found", smtpFromDB);
        assertEquals("Failure - destination name are not equal", smtpFromDB.getName(), smtp.getName());
        assertNotNull("Failure - destinationParameters for SMPT should not be null", smtp.getDestinationParameters());
        assertEquals("Failure - destinationParameters for SMPT should have 5", 5, smtp.getDestinationParameters().size());
        // assertThat("results should include liabilitiesDatasource:", results, hasItem(liabilitiesDatasource));
        // assertEquals("Failure - datasource name are not equal", jdbcFromDB.getName(), jdbc.getName());
    }
}
