package com.epam.esm.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * DTO object encapsulating information about User. Used for Web Layer <- Service layer communication
 */
@Data
@SuperBuilder
@NoArgsConstructor
public class UserDTO extends IdentifiableDTO {
	private String userName;
}
