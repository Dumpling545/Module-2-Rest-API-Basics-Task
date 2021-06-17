package com.epam.esm.model.entity;

import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity object encapsulating information about Gift Certificate. Used for Service Layer <-> Repository layer
 * communication
 */
@Value
public class GiftCertificate {
	Integer id;
	String name;
	String description;
	BigDecimal price;
	Integer duration;
	LocalDateTime createDate;
	LocalDateTime lastUpdateDate;
}
