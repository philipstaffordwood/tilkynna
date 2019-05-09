/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.destination.provider;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.tilkynna.ReportingConstants;
import org.tilkynna.common.storage.ContentRepository;
import org.tilkynna.report.destination.model.db.DestinationEntity;
import org.tilkynna.report.generate.model.db.GeneratedReportEntity;

@Service(ReportingConstants.REPORT_OUTPUTTO_STREAM)
public class StreamDestinationProvider implements DestinationProvider {

    @Autowired
    @Qualifier("streamDestinationRepository")
    private ContentRepository streamDestinationRepository;

    @Override
    public void write(GeneratedReportEntity reportRequest, byte[] reportFile) throws IOException {
        streamDestinationRepository.store(reportFile, reportRequest.getCorrelationId());
    }

    public Resource read(UUID correlationId) throws IOException {
        return streamDestinationRepository.loadAsResource(correlationId);
    }

    @Override
    public boolean testConnection(DestinationEntity destination) {
        return true; // streamed resource does not really have a connection to test
    }
}
