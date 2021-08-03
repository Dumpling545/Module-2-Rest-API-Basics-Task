package com.epam.esm.model.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

import static com.epam.esm.model.dto.ValidationConstraints.MAX_TAG_NAME_LENGTH;
import static com.epam.esm.model.dto.ValidationConstraints.MIN_TAG_NAME_LENGTH;

/**
 * DTO object encapsulating information about Gift Certificate filters. Used for Web Layer <-> Service layer
 * communication
 */
@Data
@Builder
public class GiftCertificateSearchFilterDTO {
	private String namePart;
	private String descriptionPart;
	private Set<@NotNull(message = "{tag.validation-message.name-not-empty}") @Size(min = MIN_TAG_NAME_LENGTH,
	                                                                                max = MAX_TAG_NAME_LENGTH,
	                                                                                message = "{tag.validation-message.name-size}") String>
			tagNames;
}
