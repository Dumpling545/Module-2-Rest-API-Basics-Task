package com.epam.esm.model.dto;

import com.epam.esm.model.entity.Filter;
import lombok.Value;

import java.io.Serial;
import java.io.Serializable;

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
