/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.destination.integration;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SFTPConfigSettings {

    private String host;
    private Short port;
    private String username;
    private byte[] password;
    private String workingDirectory;
}
