package com.epam.esm.model.dto;

import lombok.Value;

import java.util.Set;

/**
 * DTO object encapsulating information for updates of Gift Certificates. Used for Web Layer -> Service layer
 * communication
 */
@Value
public class GiftCertificateUpdateDTO {
	Integer id;
	String name;
	String description;
	Double price;
	Integer duration;
	Set<String> tags;
}
