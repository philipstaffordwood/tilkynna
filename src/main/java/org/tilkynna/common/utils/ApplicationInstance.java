/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.common.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

public final class ApplicationInstance {

    public static final String UNKNOWN_HOST = "Unknown";
    private static String name;

    static {
        initName();
    }

    private ApplicationInstance() {

    }

    public static String name() {
        return name;
    }

    /**
     * Only calculate the ApplicationInstance name once, so that we are not needing to call `InetAddress.getLocalHost().getHostName();` each time we want ApplicationInstance.name()
     */
    private static void initName() {
        try {
            name = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            name = UNKNOWN_HOST;
        }
    }
}
