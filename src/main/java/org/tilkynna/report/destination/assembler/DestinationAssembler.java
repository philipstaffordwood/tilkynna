/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.destination.assembler;

import java.util.ArrayList;
import java.util.List;

import org.openapitools.model.DestinationResponseBase;
import org.openapitools.model.DestinationResponseHeader;
import org.openapitools.model.DestinationResponseHeader.StatusEnum;
import org.openapitools.model.DestinationType;
import org.springframework.stereotype.Component;
import org.tilkynna.report.destination.model.db.DestinationEntity;

@Component
public class DestinationAssembler {

    public DestinationResponseBase mapDestinationEntityToDestinationResponseBase(DestinationEntity src) {
        DestinationResponseHeader header = this.mapDestinationEntityToDestinationResponseHeader(src);

        DestinationResponseBase response = new DestinationResponseBase();
        response.setDestinationType(DestinationType.valueOf(src.getType()));
        response.setHeader(header);

        return response;
    }

    public DestinationResponseHeader mapDestinationEntityToDestinationResponseHeader(DestinationEntity src) {
        DestinationResponseHeader destinationResponse = new DestinationResponseHeader();
        destinationResponse.setId(src.getDestinationId());
        destinationResponse.setName(src.getName());
        destinationResponse.setDescription(src.getDescription());
        destinationResponse.setStatus(src.isActive() ? StatusEnum.ACTIVE : StatusEnum.INACTIVE);
        destinationResponse.setDownloadable(src.isDownloadable());

        return destinationResponse;
    }

    public List<DestinationResponseHeader> mapListDestinationEntityToDestinationResponseHeader(List<DestinationEntity> src) {
        List<DestinationResponseHeader> destinations = new ArrayList<>();

        src.forEach(destination -> { //
            DestinationResponseHeader destinationResponse = this.mapDestinationEntityToDestinationResponseHeader(destination);
            destinations.add(destinationResponse);
        });

        return destinations;
    }

}
