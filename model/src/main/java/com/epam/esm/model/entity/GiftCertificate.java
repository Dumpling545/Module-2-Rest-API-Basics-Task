package com.epam.esm.model.entity;

import lombok.Value;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
