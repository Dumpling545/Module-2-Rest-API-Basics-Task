package com.epam.esm.model.entity;

import lombok.Value;

/**
 * Object encapsulating information about sorting in Filter entity. Used for Service Layer <-> Repository layer
 * communication
 */
@Value
public class SortOption {
	public enum Field {
		NAME, LAST_UPDATE_DATE, CREATE_DATE
	}

	public enum Direction {
		ASC, DESC
	}

	Field field;
	Direction direction;
}
