/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.destination.strategy;

import org.openapitools.model.DestinationCreateBase;
import org.tilkynna.report.destination.model.db.DestinationEntity;
import org.tilkynna.report.destination.model.db.StreamDestinationEntity;

public class STREAMDestinationEntityStrategy extends DestinationEntityStrategy {

    private DestinationCreateBase destination;

    public STREAMDestinationEntityStrategy(DestinationCreateBase destination) {
        this.destination = destination;
    }

    @Override
    public DestinationEntity createDestination() {
        return new StreamDestinationEntity();
    }

    @Override
    public String getType() {
        return this.destination.getDestinationType().name();
    }
}
