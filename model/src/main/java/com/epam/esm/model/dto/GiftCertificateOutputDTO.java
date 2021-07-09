package com.epam.esm.model.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Value;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO object encapsulating information about Gift Certificates. Used for Web Layer <- Service layer communication
 */
@Data
@SuperBuilder
public class GiftCertificateOutputDTO extends IdentifiableDTO {
	private String name;
	private String description;
	private BigDecimal price;
	private Integer duration;
	private LocalDateTime createDate;
	private LocalDateTime lastUpdateDate;
	private Set<TagDTO> tags;
}
