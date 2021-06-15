package com.epam.esm.model.dto;

import lombok.Value;

import java.util.HashSet;
import java.util.Set;

/**
 * DTO object encapsulating information for updates of Gift Certificates. Used for Web Layer -> Service layer
 * communication
 */
@Value
public class GiftCertificateUpdateDTO {
	String name;
	String description;
	Double price;
	Integer duration;
	Set<String> tagNames = new HashSet<>();
}
