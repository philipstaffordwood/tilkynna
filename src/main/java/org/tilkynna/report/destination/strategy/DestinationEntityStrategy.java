/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.destination.strategy;

import org.tilkynna.report.destination.model.db.DestinationEntity;

public abstract class DestinationEntityStrategy {
	public abstract DestinationEntity createDestination();

	public abstract String getType();
}
