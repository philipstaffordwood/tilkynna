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
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Service;
import org.tilkynna.ReportingConstants;
import org.tilkynna.common.error.ResourceNotFoundExceptions;
import org.tilkynna.report.destination.integration.DynamicSftpChannelResolver;
import org.tilkynna.report.destination.integration.SFTPConfigSettings;
import org.tilkynna.report.destination.model.dao.DestinationEntityRepository;
import org.tilkynna.report.destination.model.db.DestinationEntity;
import org.tilkynna.report.destination.model.db.SFTPDestinationEntity;
import org.tilkynna.report.generate.model.db.GeneratedReportEntity;

@Service(ReportingConstants.SFTP)
public class SftpDestinationProvider implements DestinationProvider {

    @Autowired
    private DestinationEntityRepository destinationRepository;

    @Autowired
    private DynamicSftpChannelResolver dynamicSftpChannelResolver;

    private SFTPConfigSettings extractSftpConfig(GeneratedReportEntity reportRequest, SFTPDestinationEntity sftp) {
        SFTPConfigSettings sftpConfig = new SFTPConfigSettings();
        sftpConfig.setDestinationId(sftp.getDestinationId());
        sftpConfig.setUpdatedOn(sftp.getUpdatedOn());
        sftpConfig.setHost(sftp.getHost());
        sftpConfig.setPort(sftp.getPort());
        sftpConfig.setUsername(sftp.getUsername());
        sftpConfig.setPassword(sftp.getPassword());

        reportRequest.getSelectedDestinationParameters().forEach( //
                sdp -> sftpConfig.setWorkingDirectory(sftp.getWorkingDirectory() + "/" + sdp.getValue()));

        return sftpConfig;
    }

    private DestinationEntity getDestinationEntity(UUID destinationId) {
        return destinationRepository.findById(destinationId) //
                .orElseThrow(() -> new ResourceNotFoundExceptions.Destination(destinationId.toString()));
    }

    @Override
    public void write(GeneratedReportEntity reportRequest, byte[] reportFile) throws IOException {
        UUID destinationId = reportRequest.getDestination().getDestinationId();
        SFTPDestinationEntity sftp = (SFTPDestinationEntity) getDestinationEntity(destinationId);
        SFTPConfigSettings sftpConfig = extractSftpConfig(reportRequest, sftp);

        MessageChannel channel = dynamicSftpChannelResolver.resolve(sftpConfig);

        Message<byte[]> message = //
                MessageBuilder.withPayload(reportFile) //
                        .setCorrelationId(reportRequest.getCorrelationId()) //
                        .setHeader("filename", reportRequest.getCorrelationId()) // TODO setting the file name :: RM #856
                        .setHeader("extension", reportRequest.getExportFormat().getName()) //
                        .build();

        channel.send(message);
    }

    @Override
    public boolean testConnection(DestinationEntity destination) {
        SFTPDestinationEntity destination2Test = (SFTPDestinationEntity) destination;

        SFTPConfigSettings sftpConfig = new SFTPConfigSettings();
        sftpConfig.setHost(destination2Test.getHost());
        sftpConfig.setPort(destination2Test.getPort());
        sftpConfig.setUsername(destination2Test.getUsername());
        sftpConfig.setPassword(destination2Test.getPassword());

        return dynamicSftpChannelResolver.test(sftpConfig);
    }
}
