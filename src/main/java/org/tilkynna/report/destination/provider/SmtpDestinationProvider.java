/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.destination.provider;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tilkynna.ReportingConstants;
import org.tilkynna.report.destination.model.dao.DestinationEntityRepository;
import org.tilkynna.report.destination.model.db.DestinationEntity;
import org.tilkynna.report.destination.model.db.SMTPDestinationEntity;
import org.tilkynna.report.generate.model.db.GeneratedReportEntity;

@Service(ReportingConstants.SMTP)
public class SmtpDestinationProvider implements DestinationProvider {

    @Autowired
    private DestinationEntityRepository destinationRepository;

    @Override
    public void write(GeneratedReportEntity reportRequest, byte[] reportFile) throws IOException {
        UUID destinationId = reportRequest.getDestination().getDestinationId();
        Optional<DestinationEntity> destinationEntity = destinationRepository.findById(destinationId);
        destinationEntity.isPresent();

        SMTPDestinationEntity smtp = (SMTPDestinationEntity) destinationEntity.get();

    }

    @Override
    public boolean testConnection(DestinationEntity destination) {
        return false;
    }
}
