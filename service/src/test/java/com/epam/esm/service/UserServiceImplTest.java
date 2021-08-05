package com.epam.esm.service;

import com.epam.esm.db.UserRepository;
import com.epam.esm.model.dto.UserDTO;
import com.epam.esm.model.entity.User;
import com.epam.esm.service.converter.UserConverter;
import com.epam.esm.service.exception.InvalidUserException;
import com.epam.esm.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.data.util.TypeInformation;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.EMPTY_LIST;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserServiceImplTest {
	private static final int ID = -1;
	private static final String EXISTING_CLIENT = "existing_client";
	private static final String NON_EXISTING_CLIENT = "non_existing_client";
	private static final UserDTO existingUserDto1 = UserDTO.builder()
			.id(999)
			.name("user")
			.externalId("exid")
			.externalProvider("expr")
			.build();
	private static final User existingUser1 = User.builder()
			.id(existingUserDto1.getId())
			.name(existingUserDto1.getName())
			.externalId(existingUserDto1.getExternalId())
			.externalProvider(existingUserDto1.getExternalProvider())
			.build();
	private static final UserDTO existingUserDto2 = UserDTO.builder()
			.id(919)
			.name("user2").build();
	private static final User existingUser2 = User.builder()
			.id(existingUserDto2.getId())
			.name(existingUserDto2.getName()).build();
	private static final List<User> allExistingUserList = List.of(existingUser1, existingUser2);
	private static final Slice<User> sliceWithUsers = new SliceImpl<>(allExistingUserList);
	private static final UserDTO newPasswordBasedUserDto = UserDTO.builder()
			.id(12)
			.name("user3")
			.password("1234")
			.build();
	private static final UserDTO expectedNewPasswordBasedUserDto = UserDTO.builder()
			.id(newPasswordBasedUserDto.getId())
			.name(newPasswordBasedUserDto.getName())
			.password(newPasswordBasedUserDto.getPassword())
			.role(UserDTO.Role.USER)
			.build();
	private static final UserDTO newExternalProviderBasedUserDto = UserDTO.builder()
			.id(13)
			.name("user4")
			.externalId("12345")
			.externalProvider(EXISTING_CLIENT)
			.build();
	private static final UserDTO expectedNewExternalProviderBasedUserDto = UserDTO.builder()
			.id(newExternalProviderBasedUserDto.getId())
			.name(newExternalProviderBasedUserDto.getName())
			.externalId(newExternalProviderBasedUserDto.getExternalId())
			.externalProvider(newExternalProviderBasedUserDto.getPassword())
			.role(UserDTO.Role.USER)
			.build();
	private static final UserDTO inconsistentCredentialsUserDto = UserDTO.builder()
			.id(13)
			.name("user4")
			.password("1234")
			.externalProvider(EXISTING_CLIENT)
			.build();
	private static final Pageable existingPageable = Pageable.ofSize(10);
	private static final List<UserDTO> allExistingUserDtoList = List.of(existingUserDto1, existingUserDto2);
	private static final Slice<UserDTO> sliceWithUserDTOs = new SliceImpl<>(allExistingUserDtoList);
	private static final UserDTO nonExistingUserDto = UserDTO.builder()
			.id(-10)
			.name("non existent").build();
	private static final String USER_NAME_2 = "user name 2";
	private static final String ALREADY_EXISTS_MESSAGE_FIELD_NAME = "alreadyExistsExceptionTemplate";
	private static final String NOT_FOUND_MESSAGE_FIELD_NAME = "notFoundExceptionTemplate";
	private static final String INVALID_FIELD_TOKEN_TEMPLATE_FIELD_NAME = "invalidFieldTokenTemplate";
	private static final String INCONSISTENT_CREDENTIALS_TEMPLATE_FIELD_NAME = "inconsistentCredentialsTemplate";
	private static final String MOCK_EX_MESSAGE = "test";

	private static final int EXISTING_CERT_ID = 1;
	private static final int NON_EXISTING_CERT_ID = 1;
	private static final UserConverter userConverter = Mappers.getMapper(UserConverter.class);
	private static final PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
	private static final ClientRegistrationRepository clientRepository =
			Mockito.mock(ClientRegistrationRepository.class);

	@BeforeAll
	public static void init() {
		ClientRegistration registration = Mockito.mock(ClientRegistration.class);
		Mockito.when(clientRepository.findByRegistrationId(Mockito.eq(EXISTING_CLIENT))).thenReturn(registration);
		Mockito.when(passwordEncoder.encode(Mockito.any())).thenAnswer(a -> a.getArgument(0));
	}

	@Test
	public void getUserShouldReturnDtoWhenPassedExistingUserId() {
		UserRepository userRepository = Mockito.mock(UserRepository.class);
		Mockito.when(userRepository.findById(Mockito.eq(existingUserDto1.getId())))
				.thenReturn(Optional.of(existingUser1));
		UserService userService = new UserServiceImpl(userRepository, userConverter, passwordEncoder, clientRepository);
		assertDoesNotThrow(() -> {
			UserDTO res = userService.getUser(existingUserDto1.getId());
			assertEquals(existingUserDto1, res);
		});
	}

	@Test
	public void getUserShouldThrowExceptionWhenPassedNonExistingUserId() {
		UserRepository userRepository = Mockito.mock(UserRepository.class);
		Mockito.when(userRepository.findById(Mockito.eq(nonExistingUserDto.getId()))).thenReturn(Optional.empty());
		UserService userService = new UserServiceImpl(userRepository, userConverter, passwordEncoder, clientRepository);
		ReflectionTestUtils.setField(userService, NOT_FOUND_MESSAGE_FIELD_NAME, MOCK_EX_MESSAGE, String.class);
		InvalidUserException ex =
				assertThrows(InvalidUserException.class,
		                                       () -> userService.getUser(existingUserDto1.getId()));
		assertEquals(InvalidUserException.Reason.NOT_FOUND, ex.getReason());
	}

	@Test
	public void getAllUsersShouldNotThrowExceptionAndReturnList() {
		UserRepository userRepository = Mockito.mock(UserRepository.class);
		Mockito.when(userRepository.getAllUsersBy(Mockito.eq(existingPageable)))
				.thenReturn(sliceWithUsers);
		UserService userService = new UserServiceImpl(userRepository, userConverter, passwordEncoder, clientRepository);
		assertDoesNotThrow(() -> {
			Slice<UserDTO> userDTOPagedResult = userService.getAllUsers(existingPageable);
			assertEquals(sliceWithUserDTOs, userDTOPagedResult);
		});
	}

	@Test
	public void getAllUsersShouldThrowInvalidUserExceptionWhenInnerPropertyReferenceExceptionIsThrown() {
		UserRepository userRepository = Mockito.mock(UserRepository.class);
		TypeInformation typeInformation = Mockito.mock(TypeInformation.class);
		PropertyReferenceException pre = new PropertyReferenceException(MOCK_EX_MESSAGE, typeInformation, EMPTY_LIST);
		Mockito.when(userRepository.getAllUsersBy(Mockito.any())).thenThrow(pre);
		UserService userService = new UserServiceImpl(userRepository, userConverter, passwordEncoder, clientRepository);
		ReflectionTestUtils.setField(userService, INVALID_FIELD_TOKEN_TEMPLATE_FIELD_NAME, MOCK_EX_MESSAGE,
		                             String.class);
		InvalidUserException iue = assertThrows(InvalidUserException.class,
		                                        () -> userService.getAllUsers(existingPageable));
		assertEquals(InvalidUserException.Reason.INVALID_SORT_BY, iue.getReason());
	}

	@Test
	public void registerUserShouldNotThrowExceptionWhenPassedValidPasswordBasedUser() {
		UserRepository userRepository = Mockito.mock(UserRepository.class);
		Mockito.when(userRepository.save(Mockito.any())).then(a -> a.getArgument(0));
		UserService userService = new UserServiceImpl(userRepository, userConverter, passwordEncoder, clientRepository);
		assertDoesNotThrow(() -> {
			UserDTO userDTO = userService.registerUser(newPasswordBasedUserDto);
			assertEquals(expectedNewPasswordBasedUserDto, userDTO);
		});
	}

	@Test
	public void registerUserShouldNotThrowExceptionWhenPassedValidExternalProviderBasedUser() {
		UserRepository userRepository = Mockito.mock(UserRepository.class);
		Mockito.when(userRepository.save(Mockito.any())).then(a -> a.getArgument(0));
		UserService userService = new UserServiceImpl(userRepository, userConverter, passwordEncoder, clientRepository);
		assertDoesNotThrow(() -> {
			UserDTO userDTO = userService.registerUser(newExternalProviderBasedUserDto);
			assertEquals(expectedNewExternalProviderBasedUserDto, userDTO);
		});
	}

	@Test
	public void registerUserShouldThrowExceptionWhenPassedUserWithInconsistentCredentials() {
		UserRepository userRepository = Mockito.mock(UserRepository.class);
		Mockito.when(userRepository.save(Mockito.any())).then(a -> a.getArgument(0));
		UserService userService = new UserServiceImpl(userRepository, userConverter, passwordEncoder, clientRepository);
		ReflectionTestUtils.setField(userService, INCONSISTENT_CREDENTIALS_TEMPLATE_FIELD_NAME, MOCK_EX_MESSAGE,
		                             String.class);
		InvalidUserException iue = assertThrows(InvalidUserException.class,
		                                        () -> userService.registerUser(inconsistentCredentialsUserDto));
		assertEquals(InvalidUserException.Reason.INCONSISTENT_CREDENTIALS, iue.getReason());
	}

	@Test
	public void registerUserShouldThrowInvalidUserExceptionWhenInnerDataIntegrityViolationExceptionIsThrown() {
		UserRepository userRepository = Mockito.mock(UserRepository.class);
		Mockito.when(userRepository.save(Mockito.any())).thenThrow(DataIntegrityViolationException.class);
		UserService userService = new UserServiceImpl(userRepository, userConverter, passwordEncoder, clientRepository);
		ReflectionTestUtils.setField(userService, ALREADY_EXISTS_MESSAGE_FIELD_NAME, MOCK_EX_MESSAGE, String.class);
		InvalidUserException iue = assertThrows(InvalidUserException.class,
		                                        () -> userService.registerUser(newPasswordBasedUserDto));
		assertEquals(InvalidUserException.Reason.ALREADY_EXISTS, iue.getReason());
	}

	@Test
	public void getUserByExternalsShouldReturnNonEmptyOptionalWhenPassedCorrectIdAndProvider() {
		UserRepository userRepository = Mockito.mock(UserRepository.class);
		Mockito.when(userRepository.getUserByExternalIdAndExternalProvider(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(Optional.of(existingUser1));
		UserService userService = new UserServiceImpl(userRepository, userConverter, passwordEncoder, clientRepository);
		Optional<UserDTO> fetched = userService.getUser(existingUserDto1.getExternalId(),
		                                                existingUser1.getExternalProvider());
		assertTrue(fetched.isPresent());
		assertEquals(existingUserDto1, fetched.get());
	}

	@Test
	public void getUserByExternalsShouldReturnEmptyOptionalWhenPassedEmptyId() {
		UserRepository userRepository = Mockito.mock(UserRepository.class);
		Mockito.when(userRepository.getUserByExternalIdAndExternalProvider(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(Optional.of(existingUser1));
		UserService userService = new UserServiceImpl(userRepository, userConverter, passwordEncoder, clientRepository);
		Optional<UserDTO> fetched = userService.getUser("", existingUser1.getExternalProvider());
		assertTrue(fetched.isEmpty());
	}

	@Test
	public void getUserByExternalsShouldReturnEmptyOptionalWhenPassedEmptyProvider() {
		UserRepository userRepository = Mockito.mock(UserRepository.class);
		Mockito.when(userRepository.getUserByExternalIdAndExternalProvider(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(Optional.of(existingUser1));
		UserService userService = new UserServiceImpl(userRepository, userConverter, passwordEncoder, clientRepository);
		Optional<UserDTO> fetched = userService.getUser(existingUser1.getExternalId(), "");
		assertTrue(fetched.isEmpty());
	}

	@Test
	public void getUserShouldReturnOptionalWithDtoWhenPassedExistingUserName() {
		UserRepository userRepository = Mockito.mock(UserRepository.class);
		Mockito.when(userRepository.getUserByName(Mockito.anyString())).thenReturn(Optional.of(existingUser1));
		UserService userService = new UserServiceImpl(userRepository, userConverter, passwordEncoder, clientRepository);
		assertDoesNotThrow(() -> {
			Optional<UserDTO> res = userService.getUser(existingUserDto1.getName());
			assertTrue(res.isPresent());
			assertEquals(existingUserDto1, res.get());
		});
	}
}
