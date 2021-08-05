package com.epam.esm.service.impl;

import com.epam.esm.db.UserRepository;
import com.epam.esm.model.dto.UserDTO;
import com.epam.esm.model.entity.User;
import com.epam.esm.service.UserService;
import com.epam.esm.service.converter.UserConverter;
import com.epam.esm.service.exception.InvalidUserException;
import com.epam.esm.service.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;
	private final UserConverter userConverter;
	private final PasswordEncoder passwordEncoder;
	private final ClientRegistrationRepository clientRegistrationRepository;
	@Value("${user.exception.not-found}")
	private String notFoundExceptionTemplate;
	@Value("${user.exception.already-exists}")
	private String alreadyExistsExceptionTemplate;
	@Value("${user.exception.inconsistent-credentials}")
	private String inconsistentCredentialsTemplate;
	@Value("${user.exception.sort-by.invalid-field}")
	private String invalidFieldTokenTemplate;

	@Override
	public UserDTO getUser(int id) {
		Optional<User> userOptional;
		try {
			userOptional = userRepository.findById(id);
		} catch (DataAccessException ex) {
			throw new ServiceException(ex);
		}
		return userOptional.map(userConverter::convert).orElseThrow(() -> {
			String identifier = "id=" + id;
			String message = String.format(notFoundExceptionTemplate, identifier);
			return new InvalidUserException(message, InvalidUserException.Reason.NOT_FOUND, id);
		});
	}

	@Override
	public Optional<UserDTO> getUser(String name) {
		try {
			return userRepository.getUserByName(name).map(userConverter::convert);
		} catch (DataAccessException ex) {
			throw new ServiceException(ex);
		}
	}

	@Override
	public Optional<UserDTO> getUser(String externalId, String externalProvider) {
		if (!StringUtils.hasLength(externalId) || !StringUtils.hasLength(externalProvider)) {
			return Optional.empty();
		}
		try {
			return userRepository.getUserByExternalIdAndExternalProvider(externalId, externalProvider)
					.map(userConverter::convert);
		} catch (DataAccessException ex) {
			throw new ServiceException(ex);
		}
	}

	@Override
	public Slice<UserDTO> getAllUsers(Pageable pageable) {
		Slice<User> slice;
		try {
			slice = userRepository.getAllUsersBy(pageable);
		} catch (PropertyReferenceException ex) {
			String message = String.format(invalidFieldTokenTemplate, ex.getPropertyName());
			throw new InvalidUserException(message, ex, InvalidUserException.Reason.INVALID_SORT_BY);
		} catch (DataAccessException ex) {
			throw new ServiceException(ex);
		}
		return slice.map(userConverter::convert);
	}

	@Override
	public UserDTO registerUser(UserDTO userDto) {
		User user = userConverter.convert(userDto);
		user.setId(null);
		user.setRole(User.Role.USER);
		if (isRegistrationDirect(user)) {
			user.setPassword(passwordEncoder.encode(user.getPassword()));
		} else if (!isRegistrationProviderBased(user)) {
			String message = String.format(inconsistentCredentialsTemplate, userDto.getName());
			throw new InvalidUserException(message, InvalidUserException.Reason.INCONSISTENT_CREDENTIALS);
		}
		try {
			User newUser = userRepository.save(user);
			return userConverter.convert(newUser);
		} catch (DataIntegrityViolationException ex) {
			String message = String.format(alreadyExistsExceptionTemplate, userDto.getName());
			throw new InvalidUserException(message, ex, InvalidUserException.Reason.ALREADY_EXISTS, userDto.getName());
		} catch (DataAccessException ex) {
			throw new ServiceException(ex);
		}
	}

	private boolean isRegistrationDirect(User user) {
		return StringUtils.hasLength(user.getPassword())
		       && user.getExternalId() == null
		       && user.getExternalProvider() == null;
	}

	private boolean isRegistrationProviderBased(User user) {
		return StringUtils.hasLength(user.getExternalId())
		       && StringUtils.hasLength(user.getExternalProvider())
		       && clientRegistrationRepository.findByRegistrationId(user.getExternalProvider()) != null
		       && user.getPassword() == null;
	}
}
