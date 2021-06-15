package com.epam.esm.model.entity;

import lombok.Value;

import java.time.LocalDateTime;

/**
 * Entity object encapsulating information about Gift Certificate. Used for Service Layer <-> Repository layer
 * communication
 */
@Value
public class GiftCertificate {
	int id;
	String name;
	String description;
	double price;
	int duration;
	LocalDateTime createDate;
	LocalDateTime lastUpdateDate;
}
