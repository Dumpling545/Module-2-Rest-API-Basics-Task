package com.epam.esm.service.impl;

import com.epam.esm.db.TagRepository;
import com.epam.esm.db.UserRepository;
import com.epam.esm.model.dto.TagDTO;
import com.epam.esm.model.dto.UserDTO;
import com.epam.esm.model.entity.Tag;
import com.epam.esm.model.entity.User;
import com.epam.esm.service.UserService;
import com.epam.esm.service.converter.TagConverter;
import com.epam.esm.service.converter.UserConverter;
import com.epam.esm.service.exception.InvalidTagException;
import com.epam.esm.service.exception.InvalidUserException;
import com.epam.esm.service.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	@Value("${user.exception.not-found}")
	private String notFoundExceptionTemplate;

	private final UserRepository userRepository;
	private final  UserConverter userConverter;

	@Override
	public UserDTO getUser(int id) {
		Optional<User> userOptional = Optional.empty();
		try {
			userOptional = userRepository.getUserById(id);
		} catch (DataAccessException ex) {
			throw new ServiceException(ex);
		}
		UserDTO userDTO = userOptional.map(userConverter::convert).orElseThrow(() -> {
			String identifier = "id=" + id;
			String message = String.format(notFoundExceptionTemplate, identifier);
			return new InvalidUserException(message, InvalidUserException.Reason.NOT_FOUND, id);
		});
		return userDTO;
	}

	@Override
	public List<UserDTO> getAllUsers() {
		List<User> userList = Collections.EMPTY_LIST;
		try {
			userList = userRepository.getAllUsers();
		} catch (DataAccessException ex) {
			throw new ServiceException(ex);
		}
		List<UserDTO> dtoList = userList.stream().map(userConverter::convert).toList();
		return dtoList;
	}
}
