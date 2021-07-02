package com.epam.esm.db;

import com.epam.esm.model.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
	Optional<User> getUserById(int id);
	List<User> getAllUsers();
}
