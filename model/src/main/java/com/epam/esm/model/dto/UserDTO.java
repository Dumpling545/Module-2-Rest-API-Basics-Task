package com.epam.esm.model.dto;

import com.epam.esm.model.validation.ValidPassword;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static com.epam.esm.model.dto.ValidationConstraints.MAX_USER_NAME_LENGTH;
import static com.epam.esm.model.dto.ValidationConstraints.MIN_USER_NAME_LENGTH;

/**
 * DTO object encapsulating information about User. Used for Web Layer <-> Service layer communication
 */
@Data
@SuperBuilder
@NoArgsConstructor
public class UserDTO extends IdentifiableDTO {
    @NotNull(message = "{user.validation-message.name-not-empty}")
    @Size(min = MIN_USER_NAME_LENGTH, max = MAX_USER_NAME_LENGTH, message = "{user.validation-message.name-size}")
    private String userName;
    @ValidPassword
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull(message = "{user.validation-message.password-not-empty}")
    private transient String password;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Role role;
    @JsonIgnore
    private transient String externalProvider;
    @JsonIgnore
    private transient String externalId;

    public enum Role {
        USER, ADMIN;
    }
}
