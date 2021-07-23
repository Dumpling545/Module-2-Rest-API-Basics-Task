package com.epam.esm.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static com.epam.esm.model.dto.ValidationConstraints.MAX_TAG_NAME_LENGTH;
import static com.epam.esm.model.dto.ValidationConstraints.MIN_TAG_NAME_LENGTH;

/**
 * DTO object encapsulating information about Tag. Used for Web Layer <-> Service layer communication
 */
@Data
@SuperBuilder
@NoArgsConstructor
public class TagDTO extends IdentifiableDTO {
	@NotNull(message = "{tag.validation-message.name-not-empty}")
	@Size(min = MIN_TAG_NAME_LENGTH, max = MAX_TAG_NAME_LENGTH, message="{tag.validation-message.name-size}")
	private String name;
}
