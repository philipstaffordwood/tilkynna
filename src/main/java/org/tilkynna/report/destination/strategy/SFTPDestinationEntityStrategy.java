/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.destination.strategy;

import org.openapitools.model.DestinationCreateBase;
import org.openapitools.model.DestinationCreateHeader;
import org.openapitools.model.DestinationCreateSFTP;
import org.tilkynna.report.destination.model.db.DestinationEntity;
import org.tilkynna.report.destination.model.db.SFTPDestinationEntity;

public class SFTPDestinationEntityStrategy extends DestinationEntityStrategy {

    private DestinationCreateSFTP destination;

    private String host;
    private Integer port = 22;
    private String user;
    private String pass;

    public SFTPDestinationEntityStrategy(DestinationCreateBase destination) {
        this.destination = (DestinationCreateSFTP) destination;

        this.host = this.destination.getHost();
        this.port = this.destination.getPort();
        this.user = this.destination.getUser();
        this.pass = this.destination.getPassword();
    }

    @Override
    public DestinationEntity createDestination() {
        DestinationCreateHeader header = destination.getHeader();

        SFTPDestinationEntity sftp = new SFTPDestinationEntity();
        sftp.setName(header.getName());
        sftp.setSecurityProtocol(header.getSecurityProtocol().name());

        sftp.setHost(host);
        sftp.setPort(port.shortValue());
        sftp.setUsername(user);
        sftp.setPassword(pass.getBytes());
        sftp.setDescription(header.getDescription());
        sftp.setDownloadable(header.getDownloadable());
        sftp.setWorkingDirectory(destination.getPath());

        return sftp;
    }

    @Override
    public String getType() {
        return this.destination.getDestinationType().name();
    }

}
