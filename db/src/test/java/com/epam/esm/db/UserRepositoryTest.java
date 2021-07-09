package com.epam.esm.db;

import com.epam.esm.GiftCertificateSystemApplication;
import com.epam.esm.model.entity.PagedResult;
import com.epam.esm.model.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith({SpringExtension.class})
@SpringBootTest(classes = GiftCertificateSystemApplication.class)
@AutoConfigureTestDatabase
public class UserRepositoryTest {
	private static final User nonExistingUser1 = new User(-1, "not exists");
	private static final User existingUser1 = new User(1, "user1");
	private static final int ALL_USERS_EXISTING_OFFSET = 0;
	private static final int ALL_USERS_EXISTING_LIMIT = 5;
	private static final int ALL_USERS_NON_EXISTING_OFFSET = 100;
	private static final int ALL_USERS_NON_EXISTING_LIMIT = 5;

	@Autowired
	UserRepository userRepository;

	@Test
	public void getUserByIdShouldReturnOptionalWithUserWhenPassedExistingUserId() {
		Optional<User> optional = userRepository.getUserById(existingUser1.getId());
		assertEquals(existingUser1, optional.get());
	}

	@Test
	public void getUserByIdShouldReturnEmptyOptionalWhenPassedNonExistingUserId() {
		Optional<User> optional = userRepository.getUserById(nonExistingUser1.getId());
		assertTrue(optional.isEmpty());
	}

	@Test
	public void getAllUsersShouldReturnNonEmptyListWhenPassedExistingOffsetAndLimit() {
		PagedResult<User> users = userRepository.getAllUsers(ALL_USERS_EXISTING_OFFSET, ALL_USERS_EXISTING_LIMIT);
		assertFalse(users.getPage().isEmpty());
	}

	@Test
	public void getAllUsersShouldReturnEmptyListWhenPassedNonExistingOffsetAndLimit() {
		PagedResult<User> users = userRepository.getAllUsers(ALL_USERS_NON_EXISTING_OFFSET,
				ALL_USERS_NON_EXISTING_LIMIT);
		assertTrue(users.getPage().isEmpty());
	}


}
