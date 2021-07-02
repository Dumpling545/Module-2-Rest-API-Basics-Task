package com.epam.esm.model.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO object encapsulating information about Gift Certificates. Used for Web Layer <- Service layer communication
 */
@Data
@Builder
public class GiftCertificateOutputDTO {
	Integer id;
	String name;
	String description;
	BigDecimal price;
	Integer duration;
	LocalDateTime createDate;
	LocalDateTime lastUpdateDate;
	Set<TagDTO> tags;
}
