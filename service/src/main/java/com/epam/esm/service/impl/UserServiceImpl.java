package com.epam.esm.service.impl;

import com.epam.esm.db.UserRepository;
import com.epam.esm.model.dto.PageDTO;
import com.epam.esm.model.dto.PagedResultDTO;
import com.epam.esm.model.dto.UserDTO;
import com.epam.esm.model.entity.PagedResult;
import com.epam.esm.model.entity.User;
import com.epam.esm.service.UserService;
import com.epam.esm.service.converter.PagedResultConverter;
import com.epam.esm.service.converter.UserConverter;
import com.epam.esm.service.exception.InvalidUserException;
import com.epam.esm.service.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;
	private final UserConverter userConverter;
	private final PagedResultConverter pagedResultConverter;
	@Value("${user.exception.not-found}")
	private String notFoundExceptionTemplate;

	@Override
	public UserDTO getUser(int id) {
		Optional<User> userOptional;
		try {
			userOptional = userRepository.getUserById(id);
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
	public PagedResultDTO<UserDTO> getAllUsers(PageDTO page) {
		PagedResult<User> pagedResult;
		try {
			pagedResult = userRepository.getAllUsers(page.getOffset(), page.getPageSize());
		} catch (DataAccessException ex) {
			throw new ServiceException(ex);
		}
		return pagedResultConverter.convertToUserPage(pagedResult);
	}
}
