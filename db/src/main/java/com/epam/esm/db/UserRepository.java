package com.epam.esm.db;

import com.epam.esm.model.entity.PagedResult;
import com.epam.esm.model.entity.User;

import java.util.Optional;

/**
 * Interface for managing User objects in database
 */
public interface UserRepository {
    /**
     * Retrieves user with given id
     *
     * @param id id of user to be retrieved
     * @return {@link Optional} containing corresponding user object, if user with such id exists in database;
     * empty {@link Optional} otherwise
     */
    Optional<User> getUserById(int id);

    /**
     * Retrieves all users in database
     *
     * @param offset how many elements to skip
     * @param limit  how many elements to retrieve
     * @return paged list of users
     */
    PagedResult<User> getAllUsers(int offset, int limit);
}
