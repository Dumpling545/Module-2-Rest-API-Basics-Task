package com.epam.esm.model.entity;

import lombok.Value;

import java.io.Serial;
import java.io.Serializable;

/**
 * Entity object encapsulating information about Gift Certificate filters. Used for Service Layer <-> Repository layer
 * communication
 */
@Value
public class Filter {
	String namePart;
	String descriptionPart;
	String tagName;
	SortOption sortBy;
}
