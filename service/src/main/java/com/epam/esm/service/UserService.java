package com.epam.esm.service;

import com.epam.esm.model.dto.UserDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.validation.annotation.Validated;

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
	 * @param name name of user to be retrieved
	 * @return Optional with user matching provided name; empty Optional otherwise.
	 */
	Optional<UserDTO> getUser(String name);

	/**
	 * Retrieves user object registered with external provider.
	 *
	 * @param externalId       -- String that uniquely identifies user in provider system
	 * @param externalProvider -- provider name (github, google, etc.)
	 * @return Optional with user matching provided external id and provider name; empty Optional otherwise.
	 * @apiNote Method must return {@link Optional#empty()} if at least one of params
	 * is null or empty
	 */
	Optional<UserDTO> getUser(String externalId, String externalProvider);

	/**
	 * Retrieves all existing users from database
	 *
	 * @param pageable paging info
	 * @return paged list of users
	 */
	Slice<UserDTO> getAllUsers(Pageable pageable);

	/**
	 * Registers user
	 *
	 * @param user dto representing user to be registered, id is ignored
	 * @return dto representing created tag
	 */
	UserDTO registerUser(UserDTO user);
}
