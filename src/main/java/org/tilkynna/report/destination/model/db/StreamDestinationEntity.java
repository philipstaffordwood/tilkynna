/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.destination.model.db;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "stream")
public class StreamDestinationEntity extends DestinationEntity {

    public StreamDestinationEntity() {
        super();
        this.setType(DestinationTypes.Values.STREAM);
    }

    @Override
    public void setDefaultDestinationParameters() {
        // has no destination parameters
    }
}
