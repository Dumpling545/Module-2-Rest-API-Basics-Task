package com.epam.esm.model.dto;

import lombok.Value;

import javax.validation.constraints.Size;

import static com.epam.esm.model.dto.ValidationConstraints.MAX_TAG_NAME_LENGTH;
import static com.epam.esm.model.dto.ValidationConstraints.MIN_TAG_NAME_LENGTH;

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
