package com.epam.esm.service;

import com.epam.esm.model.dto.PageDTO;
import com.epam.esm.model.dto.PagedResultDTO;
import com.epam.esm.model.dto.UserDTO;

import javax.validation.Valid;

/**
 * Interface of service for manipulating User DTO objects
 */
public interface UserService {
	/**
	 * Retrieves user object by given id
	 *
	 * @param id if of user to be retrieved
	 * @return user matching provided id
	 */
	UserDTO getUser(int id);

	/**
	 * Retrieves all existing users from database
	 *
	 * @param page paging info
	 * @return paged list of users
	 */
	PagedResultDTO<UserDTO> getAllUsers(@Valid PageDTO page);
}
