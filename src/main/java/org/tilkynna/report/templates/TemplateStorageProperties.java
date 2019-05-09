/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.templates;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("tilkynna.templates")
public class TemplateStorageProperties {

    /**
     * Folder location for storing templates
     */
    private String location = "tmp/templates";

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}
