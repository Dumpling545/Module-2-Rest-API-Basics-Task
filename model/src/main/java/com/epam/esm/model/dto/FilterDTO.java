package com.epam.esm.model.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Value;

/**
 * DTO object encapsulating information about Gift Certificate filters. Used for Web Layer <-> Service layer
 * communication
 */
@Data
@Builder
public class FilterDTO {
	String namePart;
	String descriptionPart;
	String tagName;
	String sortBy;
}
