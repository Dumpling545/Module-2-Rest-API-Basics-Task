package com.epam.esm.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Super class for DTO objects, used for Web Layer <-> Service layer
 * communication
 */
@Data
@SuperBuilder
@NoArgsConstructor
public abstract class IdentifiableDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    protected Integer id;
}
