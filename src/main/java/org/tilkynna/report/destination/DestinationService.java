/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.destination;

import java.util.List;
import java.util.UUID;

import org.openapitools.model.DestinationResponseBase;
import org.openapitools.model.DestinationResponseHeader;
import org.openapitools.model.LookupDestinationType;
import org.tilkynna.report.destination.strategy.DestinationEntityStrategy;

public interface DestinationService {
    public abstract DestinationResponseBase createDestination(DestinationEntityStrategy createDestinationStrategy);

    public abstract void updateDestination(UUID destinationId, DestinationEntityStrategy createDestinationEntityStrategy);

    public List<LookupDestinationType> getDestinationTypes();

    public List<DestinationResponseHeader> findAll();

    public DestinationResponseBase getDestination(UUID destinationId);

    public boolean validateConnection(UUID destinationId);

    public void inactivateDestination(UUID datasourceId);
}
