package com.epam.esm.model.entity;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

/**
 * Entity object encapsulating information about Gift Certificate filters. Used for Service Layer <-> Repository layer
 * communication
 */
@Data
@Builder
public class GiftCertificateSearchFilter {
	private String namePart;
	private String descriptionPart;
	private Set<String> tagNames;
}
