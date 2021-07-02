package com.epam.esm.service;

import com.epam.esm.model.dto.UserDTO;
import java.util.List;

public interface UserService {
	UserDTO getUser(int id);
	List<UserDTO> getAllUsers();
}
