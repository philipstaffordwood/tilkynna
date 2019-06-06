/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.common.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

@Component
public class ApplicationInstance {

    private static String name;

    private ApplicationInstance() {
    }

    public static String name() {
        return name;
    }

    /**
     * Only calculate the ApplicationInstance name once, so that we are not needing to call `InetAddress.getLocalHost().getHostName();` each time we want ApplicationInstance.name()
     */
    @PostConstruct
    private void initName() {
        try {
            name = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            name = "Unknown";
        }
    }
}
