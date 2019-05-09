/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.destination.strategy;

import org.openapitools.model.DestinationCreateBase;
import org.springframework.stereotype.Component;

@Component
public class DestinationEntityStrategyFactory {

    public DestinationEntityStrategy createStrategy(DestinationCreateBase destination) {
        DestinationEntityStrategy strategy = null;

        switch (destination.getDestinationType()) {
        case SFTP:
            strategy = new SFTPDestinationEntityStrategy(destination);
            break;
        case SMTP:
            strategy = new SMTPDestinationEntityStrategy(destination);
            break;
        case STREAM:
            strategy = new STREAMDestinationEntityStrategy(destination);
            break;
        default:
            break;
        }

        return strategy;
    }
}
