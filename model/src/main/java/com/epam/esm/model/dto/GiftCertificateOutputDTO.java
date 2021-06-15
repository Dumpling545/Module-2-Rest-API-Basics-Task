package com.epam.esm.model.dto;

import lombok.Setter;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * DTO object encapsulating information about Gift Certificates. Used for Web Layer <- Service layer communication
 */
@Value
public class GiftCertificateOutputDTO {
	Integer id;
	String name;
	String description;
	Double price;
	Integer duration;
	LocalDateTime createDate;
	LocalDateTime lastUpdateDate;
	Set<TagDTO> tags;
}
