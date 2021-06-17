package com.epam.esm.model.dto;

import lombok.Value;

/**
 * DTO object encapsulating information about Tag. Used for Web Layer <-> Service layer communication
 */
@Value
public class TagDTO {
	Integer id;
	String name;
}
