package com.epam.esm.model.dto;

import lombok.Value;

/**
 * DTO object encapsulating information about Gift Certificate filters. Used for Web Layer <-> Service layer
 * communication
 */
@Value
public class FilterDTO {
	String namePart;
	String descriptionPart;
	String tagName;
	String sortBy;
}
