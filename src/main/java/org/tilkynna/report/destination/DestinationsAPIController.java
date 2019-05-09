/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.destination;

import java.util.List;
import java.util.UUID;

import org.openapitools.api.DestinationsApi;
import org.openapitools.model.DestinationCreateBase;
import org.openapitools.model.DestinationResponseBase;
import org.openapitools.model.DestinationResponseHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.tilkynna.report.destination.strategy.DestinationEntityStrategy;
import org.tilkynna.report.destination.strategy.DestinationEntityStrategyFactory;

@RestController
@PreAuthorize("hasRole('TILKYNNA_USER') or hasRole('TILKYNNA_ADMIN')")
public class DestinationsAPIController implements DestinationsApi {

    @Autowired
    private DestinationEntityStrategyFactory destinationStrategyFactory;

    @Autowired
    private DestinationService destinationService;

    @Override
    public ResponseEntity<DestinationResponseBase> addReportDestination(DestinationCreateBase destinationCreateBase) {
        DestinationEntityStrategy createDestinationStrategy = destinationStrategyFactory.createStrategy(destinationCreateBase);
        DestinationResponseBase destinationResponseBase = destinationService.createDestination(createDestinationStrategy);

        return new ResponseEntity<>(destinationResponseBase, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<DestinationResponseHeader>> listConfiguredDestinations(Integer page, Integer size, String filterName, String filterStatus, List<String> orderBy) {

        return new ResponseEntity<>(destinationService.findAll(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> validateDestination(UUID destinationId) {
        destinationService.validateConnection(destinationId);
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> inactivateReportDestination(UUID destinationId) {
        destinationService.inactivateDestination(destinationId);
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<DestinationResponseBase> getDestination(UUID destinationId) {
        return new ResponseEntity<>(destinationService.getDestination(destinationId), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> updateDestination(UUID destinationId, DestinationCreateBase destinationCreateBase) {
        DestinationEntityStrategy createDestinationStrategy = destinationStrategyFactory.createStrategy(destinationCreateBase);
        destinationService.updateDestination(destinationId, createDestinationStrategy);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

}
