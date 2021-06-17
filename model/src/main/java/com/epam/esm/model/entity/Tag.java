package com.epam.esm.model.entity;

import lombok.Value;

/**
 * Entity object encapsulating information about Tag. Used for Service Layer <-> Repository layer communication
 */
@Value
public class Tag {
	Integer id;
	String name;
}
