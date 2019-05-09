/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.lookup.driver;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("driver")
public class DriversLocationProperties {

    /**
     * Folder location of drivers
     */
    private String location = "drivers";

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}
