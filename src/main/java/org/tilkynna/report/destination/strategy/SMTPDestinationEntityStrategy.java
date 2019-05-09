/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.destination.strategy;

import org.openapitools.model.DestinationCreateBase;
import org.openapitools.model.DestinationCreateHeader;
import org.openapitools.model.DestinationCreateSMTP;
import org.tilkynna.report.destination.model.db.DestinationEntity;
import org.tilkynna.report.destination.model.db.SMTPDestinationEntity;

public class SMTPDestinationEntityStrategy extends DestinationEntityStrategy {

    private DestinationCreateSMTP destination;

    public SMTPDestinationEntityStrategy(DestinationCreateBase destination) {
        this.destination = (DestinationCreateSMTP) destination;

        this.host = this.destination.getHost();
        this.port = this.destination.getPort();
        this.user = this.destination.getUser();
        this.pass = this.destination.getPassword();
    }

    private String host;
    private Integer port = 22;
    private String user;
    private String pass;

    @Override
    public DestinationEntity createDestination() {
        DestinationCreateHeader header = destination.getHeader();

        SMTPDestinationEntity smtp = new SMTPDestinationEntity();
        smtp.setName(header.getName());
        smtp.setSecurityProtocol(header.getSecurityProtocol().name());

        smtp.setHost(host);
        smtp.setPort(port.shortValue());
        smtp.setUsername(user);
        smtp.setPassword(pass.getBytes());
        smtp.setFromAddress(destination.getFromAddress());
        smtp.setDescription(header.getDescription());
        smtp.setDownloadable(header.getDownloadable());

        return smtp;

    }

    @Override
    public String getType() {
        return this.destination.getDestinationType().name();
    }
}
