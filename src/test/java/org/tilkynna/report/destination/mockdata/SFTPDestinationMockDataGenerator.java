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
import org.openapitools.model.DestinationCreateSFTP;
import org.openapitools.model.DestinationType;
import org.tilkynna.report.destination.model.db.SFTPDestinationEntity;

public class SFTPDestinationMockDataGenerator {
    private SFTPDestinationMockDataGenerator() {

    }

    private static final SFTPDestinationEntity createSFTPDestinationEntity(String destinationName) {
        SFTPDestinationEntity destination = new SFTPDestinationEntity();
        destination.setActive(true);
        destination.setName(destinationName);
        destination.setDescription("description");
        destination.setTimeout(new Long(56322));
        destination.setSecurityProtocol("ssl");
        destination.setUpdatedBy(UUID.randomUUID());

        destination.setHost("sftp://localhost:5432");
        destination.setPort(new Long(23).shortValue());
        destination.setUsername("username");
        destination.setPassword("password".getBytes());
        destination.setWorkingDirectory("/workingDirectory");

        return destination;
    }

    public static final SFTPDestinationEntity setupSFTPDestinationEntity(UUID destinationId, String destinationName) {
        SFTPDestinationEntity destination = createSFTPDestinationEntity(destinationName);
        destination.setDestinationId(destinationId);

        return destination;
    }

    public static final SFTPDestinationEntity setupSFTPDestinationEntity(String destinationName) {
        SFTPDestinationEntity destination = createSFTPDestinationEntity(destinationName);
        return destination;
    }

    public static final DestinationCreateBase createDestinationCreateBase(String name) {
        DestinationCreateSFTP destinationCreateBase = new DestinationCreateSFTP();
        destinationCreateBase.setDestinationType(DestinationType.SFTP);
        destinationCreateBase.setHost("localhost");
        destinationCreateBase.setPort(22);
        destinationCreateBase.setUser("user");
        destinationCreateBase.setPassword("password");
        destinationCreateBase.setPath("/upload");

        DestinationCreateHeader header = new DestinationCreateHeader();
        header.setName(name);
        header.setDescription("description");
        header.setDownloadable(false);
        header.setTimeout(12);
        destinationCreateBase.setHeader(header);

        return destinationCreateBase;
    }
}
