/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.destination;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.openapitools.model.DestinationCreateBase;
import org.openapitools.model.DestinationResponseBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.tilkynna.common.error.AlreadyExistsExceptions;
import org.tilkynna.report.destination.assembler.DestinationAssembler;
import org.tilkynna.report.destination.mockdata.SFTPDestinationMockDataGenerator;
import org.tilkynna.report.destination.model.dao.DestinationEntityRepository;
import org.tilkynna.report.destination.provider.DestinationProvider;
import org.tilkynna.report.destination.provider.DestinationProviderConfig;
import org.tilkynna.report.destination.provider.DestinationProviderFactory;
import org.tilkynna.report.destination.provider.SftpDestinationProvider;
import org.tilkynna.report.destination.provider.StreamDestinationStorageProperties;
import org.tilkynna.report.destination.strategy.DestinationEntityStrategy;
import org.tilkynna.report.destination.strategy.DestinationEntityStrategyFactory;

import com.jcraft.jsch.ChannelSftp;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { SftpDestinationProvider.class, DestinationProviderFactory.class, DestinationServiceImpl.class, DestinationEntityStrategyFactory.class, DestinationProviderConfig.class, DestinationAssembler.class, StreamDestinationStorageProperties.class }// ,
                                                                                                                                                                                                                                                                                // //
/* initializers = ConfigFileApplicationContextInitializer.class */)
public class DestinationServiceTests {

    @MockBean
    private DestinationEntityRepository destinationEntityRepository;

    @MockBean
    private DestinationProviderFactory destinationProviderFactory;

    @Autowired
    private DestinationService destinationService;

    @Autowired
    private DestinationEntityStrategyFactory createDestinationStrategyFactory;

    @Autowired
    private DestinationProvider sftp;

    private static com.jcraft.jsch.Session jschSession = mock(com.jcraft.jsch.Session.class);

    @TestConfiguration
    static class DestinationServiceTestContextConfiguration {

        @Bean
        public DestinationService destinationService() {
            return new DestinationServiceImpl();
        }

        @Bean
        public DestinationProvider sftp() {
            return new SftpDestinationProvider();
        }
    }

    /**
     * Test method for {@link org.tilkynna.report.destination.DestinationServiceImpl#createDestination(DestinationEntityStrategy)}.
     */
    @Test(expected = AlreadyExistsExceptions.Destination.class)
    public void givenExistingDestination_whenCreate_thenThrowDestinationAlreadyExistsException() throws Exception {
        DestinationCreateBase destinationCreateBase = SFTPDestinationMockDataGenerator.createDestinationCreateBase("MyDestination");

        Mockito.when(destinationEntityRepository.existsByNameIgnoreCase("MyDestination")).thenReturn(true);
        Mockito.when(destinationProviderFactory.get(destinationCreateBase.getDestinationType().name())).thenReturn(sftp);

        DestinationEntityStrategy createDestinationStrategy = createDestinationStrategyFactory.createStrategy(destinationCreateBase);

        destinationService.createDestination(createDestinationStrategy);
    }

    @Test
    public void givenExistingDestination_whenCreate_thenValid() throws Exception {
        DestinationCreateBase destinationCreateBase = SFTPDestinationMockDataGenerator.createDestinationCreateBase("MyDestination");

        ChannelSftp channel = mock(ChannelSftp.class);
        Mockito.when(jschSession.openChannel("sftp")).thenReturn(channel);
        Mockito.when(destinationEntityRepository.existsByNameIgnoreCase("MyDestination")).thenReturn(false);
        Mockito.when(destinationProviderFactory.get(destinationCreateBase.getDestinationType().name())).thenReturn(sftp);

        DestinationEntityStrategy createDestinationStrategy = createDestinationStrategyFactory.createStrategy(destinationCreateBase);

        DestinationResponseBase destinationResponseBase = destinationService.createDestination(createDestinationStrategy);

        assertNotNull(destinationResponseBase);
    }

}
