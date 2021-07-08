package com.epam.esm.service;

import com.epam.esm.model.dto.PageDTO;
import com.epam.esm.model.dto.PagedResultDTO;
import com.epam.esm.model.dto.UserDTO;

import javax.validation.Valid;

public interface UserService {
	UserDTO getUser(int id);
	PagedResultDTO<UserDTO> getAllUsers(@Valid PageDTO page);
}
