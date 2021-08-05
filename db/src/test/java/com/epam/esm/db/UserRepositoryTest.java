package com.epam.esm.db;

import com.epam.esm.GiftCertificateSystemApplication;
import com.epam.esm.model.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith({SpringExtension.class})
@SpringBootTest(classes = GiftCertificateSystemApplication.class)
@AutoConfigureTestDatabase
public class UserRepositoryTest {
	private static final User nonExistingUser1 = User.builder()
			.id(-1)
			.name("not exists")
			.build();
	private static final User existingUser1 = User.builder()
			.id(1)
			.name("user1")
			.role(User.Role.USER)
			.externalId("external_id_1")
			.externalProvider("external_provider_1")
			.build();
	private static final Pageable allUsersExistingPageable = PageRequest.of(0, 5);
	private static final Pageable allUsersNonExistingPageable = PageRequest.of(10, 10);

	@Autowired
	UserRepository userRepository;

	@Test
	public void getUserByIdShouldReturnOptionalWithUserWhenPassedExistingUserId() {
		Optional<User> optional = userRepository.findById(existingUser1.getId());
		assertEquals(existingUser1, optional.get());
	}

	@Test
	public void getUserByIdShouldReturnEmptyOptionalWhenPassedNonExistingUserId() {
		Optional<User> optional = userRepository.findById(nonExistingUser1.getId());
		assertTrue(optional.isEmpty());
	}

	@Test
	public void getAllUsersShouldReturnNonEmptyListWhenPassedExistingOffsetAndLimit() {
		Slice<User> users = userRepository.getAllUsersBy(allUsersExistingPageable);
		assertFalse(users.isEmpty());
	}


	@Test
	public void getAllUsersShouldReturnEmptyListWhenPassedNonExistingOffsetAndLimit() {
		Slice<User> users = userRepository.getAllUsersBy(allUsersNonExistingPageable);
		assertTrue(users.isEmpty());
	}


}
