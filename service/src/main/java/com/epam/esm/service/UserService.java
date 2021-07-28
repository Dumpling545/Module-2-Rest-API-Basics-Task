package com.epam.esm.service;

import com.epam.esm.model.dto.PageDTO;
import com.epam.esm.model.dto.PagedResultDTO;
import com.epam.esm.model.dto.UserDTO;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.Optional;

/**
 * Interface of service for manipulating User DTO objects
 */
@Validated
public interface UserService {
    /**
     * Retrieves user object by given id
     *
     * @param id if of user to be retrieved
     * @return user matching provided id
     */
    UserDTO getUser(int id);

	/**
	 * Retrieves user object by given name
	 *
	 * @param name
	 * 		name of user to be retrieved
	 * @return Optional with user matching provided name; empty Optional otherwise.
	 */
	Optional<UserDTO> getUser(String name);

    /**
     * Retrieves all existing users from database
     *
     * @param page paging info
     * @return paged list of users
     */
    PagedResultDTO<UserDTO> getAllUsers(@Valid PageDTO page);
}
