/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.destination;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.openapitools.model.DestinationResponseBase;
import org.openapitools.model.DestinationResponseHeader;
import org.openapitools.model.DestinationType;
import org.openapitools.model.LookupDestinationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tilkynna.common.error.AlreadyExistsExceptions;
import org.tilkynna.common.error.CustomValidationExceptions;
import org.tilkynna.common.error.ResourceNotFoundExceptions;
import org.tilkynna.report.destination.assembler.DestinationAssembler;
import org.tilkynna.report.destination.model.dao.DestinationEntityRepository;
import org.tilkynna.report.destination.model.db.DestinationEntity;
import org.tilkynna.report.destination.model.db.DestinationTypes;
import org.tilkynna.report.destination.provider.DestinationProvider;
import org.tilkynna.report.destination.provider.DestinationProviderFactory;
import org.tilkynna.report.destination.strategy.DestinationEntityStrategy;

@Service
public class DestinationServiceImpl implements DestinationService {

    @Autowired
    private DestinationEntityRepository destinationEntityRepository;

    @Autowired
    private DestinationProviderFactory destinationProviderFactory;

    @Autowired
    private DestinationAssembler destinationAssembler;

    private DestinationEntity getDestinationEntity(UUID destinationIdRequest) {
        return destinationEntityRepository.findById(destinationIdRequest) //
                .orElseThrow(() -> new ResourceNotFoundExceptions.Destination(destinationIdRequest.toString()));
    }

    @Override
    public DestinationResponseBase getDestination(UUID destinationId) {
        DestinationEntity destinationEntity = getDestinationEntity(destinationId);

        return destinationAssembler.mapDestinationEntityToDestinationResponseBase(destinationEntity);
    }

    @Override
    public DestinationResponseBase createDestination(DestinationEntityStrategy createDestinationEntityStrategy) {
        DestinationEntity destinationEntity = createDestinationEntityStrategy.createDestination();
        if (destinationEntityRepository.existsByNameIgnoreCase(destinationEntity.getName())) {
            throw new AlreadyExistsExceptions.Destination(destinationEntity.getName());
        }

        destinationEntity.setActive(testConnection(destinationEntity));
        destinationEntityRepository.save(destinationEntity);

        return destinationAssembler.mapDestinationEntityToDestinationResponseBase(destinationEntity);
    }

    @Override
    public void updateDestination(UUID destinationId, DestinationEntityStrategy createDestinationEntityStrategy) {
        DestinationEntity existingDestination = getDestinationEntity(destinationId);
        DestinationEntity updateDestinationEntity = createDestinationEntityStrategy.createDestination();

        if (validateDestinationTypeEqual(existingDestination, updateDestinationEntity)) {
            throw new CustomValidationExceptions.UpdateDestinationDestinationTypeNotEqual(destinationId, existingDestination.getType());
        }

        updateDestinationEntity.setDestinationParameters(existingDestination.getDestinationParameters());
        updateDestinationEntity.setDestinationId(existingDestination.getDestinationId());
        updateDestinationEntity.setActive(testConnection(updateDestinationEntity));

        destinationEntityRepository.save(updateDestinationEntity);
    }

    private boolean validateDestinationTypeEqual(DestinationEntity existingDestination, DestinationEntity updateDestinationEntity) {
        return !existingDestination.getType().equalsIgnoreCase(updateDestinationEntity.getType());
    }

    @Override
    public boolean validateConnection(UUID destinationId) {
        DestinationEntity destination = getDestinationEntity(destinationId);
        boolean returnValue = testConnection(destination);

        destination.setActive(returnValue);
        destinationEntityRepository.save(destination);

        return returnValue;
    }

    @Override
    public void inactivateDestination(UUID destinationId) {
        DestinationEntity destination = getDestinationEntity(destinationId);
        destination.setActive(false);

        destinationEntityRepository.save(destination);
    }

    private boolean testConnection(DestinationEntity destination) {
        DestinationProvider destinationProvider = destinationProviderFactory.get(destination.getType());

        return destinationProvider.testConnection(destination);
    }

    @Override
    public List<LookupDestinationType> getDestinationTypes() {
        DestinationTypes[] types = DestinationTypes.values();
        types = Arrays.stream(types).filter(x -> x.isImplemented()).toArray(DestinationTypes[]::new);

        List<LookupDestinationType> destinationTypes = new ArrayList<>();
        for (int i = 0; i < types.length; i++) {
            LookupDestinationType destinationType = new LookupDestinationType().destinationType(DestinationType.valueOf(types[i].name()));
            destinationType.setInputFields(types[i].getInputFields());

            destinationTypes.add(destinationType);
        }

        return destinationTypes;
    }

    @Override
    public List<DestinationResponseHeader> findAll() {
        List<DestinationEntity> destinationEntities = (List<DestinationEntity>) destinationEntityRepository.findAll();

        return destinationAssembler.mapListDestinationEntityToDestinationResponseHeader(destinationEntities);
    }

}
