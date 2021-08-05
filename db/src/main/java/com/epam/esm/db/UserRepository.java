package com.epam.esm.db;

import com.epam.esm.model.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.Repository;

import java.util.Optional;

/**
 * Interface for managing User objects in database
 */
public interface UserRepository extends Repository<User, Integer> {
	/**
	 * Retrieves user with given name
	 *
	 * @param name name of user to be retrieved
	 * @return {@link Optional} containing corresponding user object, if user with such id exists in database;
	 * empty {@link Optional} otherwise
	 */
	Optional<User> getUserByName(String name);

	/**
	 * Retrieves user with given external id and external provider
	 *
	 * @param externalId       -- id of user in external provider system
	 * @param externalProvider -- provider name (github, google, etc.)
	 * @return {@link Optional} containing corresponding user object, if user with such external id and provider name exists in database;
	 * empty {@link Optional} otherwise
	 */
	Optional<User> getUserByExternalIdAndExternalProvider(String externalId, String externalProvider);

	/**
	 * Retrieves all users in database
	 *
	 * @param pageable paging info
	 * @return paged list of users
	 */
	Slice<User> getAllUsersBy(Pageable pageable);

	/**
	 * Creates new user in database (id property is ignored).
	 * NOTE: password should be pre-encoded
	 *
	 * @param user object containing name for new user, id field is ignored
	 * @return User object representing newly created user in database
	 */
	User save(User user);

	/**
	 * Retrieves user with given id
	 *
	 * @param id id of user to be retrieved
	 * @return {@link Optional} containing corresponding user object, if user with such id exists in database;
	 * empty {@link Optional} otherwise
	 */
	Optional<User> findById(Integer id);
}
