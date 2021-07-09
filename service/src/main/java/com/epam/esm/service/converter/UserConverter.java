package com.epam.esm.service.converter;

import com.epam.esm.model.dto.UserDTO;
import com.epam.esm.model.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel="spring")
public interface UserConverter {
	UserDTO convert(User user);
}
