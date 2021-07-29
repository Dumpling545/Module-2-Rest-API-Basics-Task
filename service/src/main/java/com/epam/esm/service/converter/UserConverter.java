package com.epam.esm.service.converter;

import com.epam.esm.model.dto.UserDTO;
import com.epam.esm.model.entity.User;
import org.mapstruct.Mapper;

/**
 * Interface mapping user entities and DTOs. Used by MapStruct to generate actual mapper class
 *
 * @see <a href="https://mapstruct.org/">MapStruct library</a>
 */
@Mapper(componentModel = "spring")
public interface UserConverter {
    UserDTO convert(User user);
	User convert(UserDTO user);
}
