/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.destination;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.openapitools.model.DestinationCreateBase;
import org.openapitools.model.DestinationResponseBase;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.tilkynna.common.error.AlreadyExistsExceptions;
import org.tilkynna.report.destination.assembler.DestinationAssembler;
import org.tilkynna.report.destination.integration.DynamicSftpChannelResolver;
import org.tilkynna.report.destination.integration.SFTPConfigSettings;
import org.tilkynna.report.destination.mockdata.SFTPDestinationMockDataGenerator;
import org.tilkynna.report.destination.model.dao.DestinationEntityRepository;
import org.tilkynna.report.destination.provider.DestinationProviderFactory;
import org.tilkynna.report.destination.provider.SftpDestinationProvider;
import org.tilkynna.report.destination.strategy.DestinationEntityStrategy;
import org.tilkynna.report.destination.strategy.SFTPDestinationEntityStrategy;
import org.tilkynna.security.SecurityContextUtility;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SecurityContextUtility.class)
public class DestinationServiceTests {
    @InjectMocks
    private DestinationServiceImpl destinationServiceUnderTest;

    @Mock
    private DestinationEntityRepository destinationEntityRepository;

    @Mock
    private DestinationProviderFactory destinationProviderFactory;

    @Spy
    private DestinationAssembler destinationAssembler = new DestinationAssembler();

    @InjectMocks
    private SftpDestinationProvider sftp;

    @Mock
    private DynamicSftpChannelResolver dynamicSftpChannelResolver;

    /**
     * Test method for {@link org.tilkynna.report.destination.DestinationServiceImpl#createDestination(DestinationEntityStrategy)}.
     */
    @Test(expected = AlreadyExistsExceptions.Destination.class)
    public void givenExistingDestination_whenCreate_thenThrowDestinationAlreadyExistsException() throws Exception {
        DestinationCreateBase destinationCreateBase = SFTPDestinationMockDataGenerator.createDestinationCreateBase("MyDestination");

        Mockito.when(destinationEntityRepository.existsByNameIgnoreCase("MyDestination")).thenReturn(true);
        Mockito.when(destinationProviderFactory.get(destinationCreateBase.getDestinationType().name())).thenReturn(sftp);

        DestinationEntityStrategy createDestinationStrategy = new SFTPDestinationEntityStrategy(destinationCreateBase);

        destinationServiceUnderTest.createDestination(createDestinationStrategy);
    }

    @Test
    public void givenExistingDestination_whenCreate_thenValid() throws Exception {
        DestinationCreateBase destinationCreateBase = SFTPDestinationMockDataGenerator.createDestinationCreateBase("MyDestination");

        PowerMockito.mockStatic(SecurityContextUtility.class);
        Mockito.when(SecurityContextUtility.getUserIdFromJwt()).thenReturn(UUID.randomUUID().toString());

        Mockito.when(destinationEntityRepository.existsByNameIgnoreCase("MyDestination")).thenReturn(false);
        Mockito.when(destinationProviderFactory.get(destinationCreateBase.getDestinationType().name())).thenReturn(sftp);
        Mockito.when(dynamicSftpChannelResolver.test(any(SFTPConfigSettings.class))).thenReturn(true);

        DestinationEntityStrategy createDestinationStrategy = new SFTPDestinationEntityStrategy(destinationCreateBase);

        DestinationResponseBase destinationResponseBase = destinationServiceUnderTest.createDestination(createDestinationStrategy);

        assertNotNull(destinationResponseBase);
    }
}
