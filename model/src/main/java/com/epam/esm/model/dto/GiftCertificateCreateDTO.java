package com.epam.esm.model.dto;

import lombok.Value;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO object encapsulating information for creation of Gift Certificates. Used for Web Layer -> Service layer
 * communication
 */
@Value
public class GiftCertificateCreateDTO {
	String name;
	String description;
	Double price;
	Integer duration;
	Set<String> tags;
}