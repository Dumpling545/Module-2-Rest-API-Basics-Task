package com.epam.esm.model.dto;

import lombok.Value;

import java.math.BigDecimal;
import java.util.Set;

/**
 * DTO object encapsulating information for creation of Gift Certificates. Used for Web Layer -> Service layer
 * communication
 */
@Value
public class GiftCertificateCreateDTO {
	String name;
	String description;
	BigDecimal price;
	Integer duration;
	Set<String> tagNames;
}
