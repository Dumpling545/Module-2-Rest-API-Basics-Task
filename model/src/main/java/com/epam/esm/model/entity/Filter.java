package com.epam.esm.model.entity;

import lombok.Builder;
import lombok.Data;

/**
 * Entity object encapsulating information about Gift Certificate filters. Used for Service Layer <-> Repository layer
 * communication
 */
@Data
@Builder
public class Filter {
	String namePart;
	String descriptionPart;
	Tag tag;
	SortOption sortBy;
}
