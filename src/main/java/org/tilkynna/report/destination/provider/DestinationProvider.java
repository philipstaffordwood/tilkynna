/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.destination.provider;

import java.io.IOException;

import org.tilkynna.report.destination.model.db.DestinationEntity;
import org.tilkynna.report.generate.model.db.GeneratedReportEntity;

public interface DestinationProvider {
    /**
     * Given an existing destination, already in DB. Write a report file to this destination.
     * 
     * @param destinationId
     *            unique ID of destination report should be sent to
     * @param reportFile
     *            the byte[] of generated report to be sent to destination
     * @throws IOException
     *             when writing to the destination fails.
     */
    public abstract void write(GeneratedReportEntity reportRequest, byte[] reportFile) throws IOException;

    /**
     * Test connection.
     */
    public abstract boolean testConnection(DestinationEntity destination);
}
