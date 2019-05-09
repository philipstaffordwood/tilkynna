/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.destination.mockdata;

import java.util.UUID;

import org.openapitools.model.DestinationCreateBase;
import org.openapitools.model.DestinationCreateHeader;
import org.openapitools.model.DestinationCreateSMTP;
import org.openapitools.model.DestinationType;
import org.tilkynna.report.destination.model.db.SMTPDestinationEntity;

public class SMTPDestinationMockDataGenerator {
    private SMTPDestinationMockDataGenerator() {

    }

    private static final SMTPDestinationEntity createSMTPDestinationEntity(String destinationName) {
        SMTPDestinationEntity destination = new SMTPDestinationEntity();
        destination.setActive(true);
        destination.setName(destinationName);
        destination.setDescription("description");
        destination.setTimeout(new Long(
                56322));
        destination.setSecurityProtocol("securityProtocol");

        destination.setHost("SMTP://localhost:5432");
        destination.setPort(new Long(
                23).shortValue());
        destination.setUsername("username");
        destination.setPassword("password".getBytes());
        destination.setFromAddress("from_addres@here.com");

        return destination;
    }

    public static final SMTPDestinationEntity setupSMTPDestinationEntity(UUID destinationId, String destinationName) {
        SMTPDestinationEntity destination = createSMTPDestinationEntity(destinationName);
        destination.setDestinationId(destinationId);

        return destination;
    }

    public static final SMTPDestinationEntity setupSMTPDestinationEntity(String destinationName) {
        SMTPDestinationEntity destination = createSMTPDestinationEntity(destinationName);
        return destination;
    }

    public static final DestinationCreateBase createDestinationCreateBase(String name) {
        DestinationCreateSMTP destinationCreateBase = new DestinationCreateSMTP();
        destinationCreateBase.setDestinationType(DestinationType.SMTP);
        destinationCreateBase.setHost("localhost");
        destinationCreateBase.setPort(22);
        destinationCreateBase.setUser("user");
        destinationCreateBase.setPassword("password");

        DestinationCreateHeader header = new DestinationCreateHeader();
        header.setName(name);
        header.setDescription("description");
        header.setDownloadable(false);
        header.setTimeout(12);
        destinationCreateBase.setHeader(header);

        return destinationCreateBase;
    }
}
