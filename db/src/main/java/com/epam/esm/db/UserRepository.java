package com.epam.esm.db;

import com.epam.esm.model.entity.PagedResult;
import com.epam.esm.model.entity.User;

import java.util.Optional;

public interface UserRepository {
	Optional<User> getUserById(int id);
	PagedResult<User> getAllUsers(int offset, int limit);
}
