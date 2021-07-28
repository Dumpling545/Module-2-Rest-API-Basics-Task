package com.epam.esm.model.dto;

import com.epam.esm.model.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 * DTO object encapsulating information about User. Used for Web Layer <- Service layer communication
 */
@Data
@SuperBuilder
@NoArgsConstructor
public class UserDTO extends IdentifiableDTO {
	private String userName;
	@JsonIgnore
	private transient String password;
	//TODO expose role?
	private Role role;

	public enum Role{
		USER, ADMIN;
	}
}
