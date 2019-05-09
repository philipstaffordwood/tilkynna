/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.destination.provider;

import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.tilkynna.common.storage.FileSystemContentRepository;

//http://www.savethecode.com/recipe-spring-boot-uncoupled-factories/
@Configuration
public class DestinationProviderConfig {

    @Bean
    public ServiceLocatorFactoryBean getDestinationProviderFactory() {
        ServiceLocatorFactoryBean slfb = new ServiceLocatorFactoryBean();
        slfb.setServiceLocatorInterface(DestinationProviderFactory.class);

        return slfb;
    }

    @Bean(name = "streamDestinationRepository")
    public FileSystemContentRepository getStreamDestinationRepository(StreamDestinationStorageProperties properties) {
        return new FileSystemContentRepository(properties.getLocation());
    }

}
