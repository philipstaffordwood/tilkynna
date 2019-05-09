/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.destination.provider;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("tilkynna.generatedreports")
public class StreamDestinationStorageProperties {

    /**
     * Folder location for storing templates
     */
    private String location = "tmp/reports";

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}
