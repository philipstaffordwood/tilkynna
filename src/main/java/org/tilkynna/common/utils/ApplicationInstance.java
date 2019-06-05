package org.tilkynna.common.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.stereotype.Component;

@Component
public class ApplicationInstance {
    private ApplicationInstance() {
    }

    public static String name() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "Unknown";
        }

    }
}
