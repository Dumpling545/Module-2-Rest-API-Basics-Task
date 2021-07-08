package com.epam.esm.web;

import com.epam.esm.model.dto.OrderDTO;
import com.epam.esm.model.dto.PageDTO;
import com.epam.esm.model.dto.UserDTO;
import com.epam.esm.service.OrderService;
import com.epam.esm.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.epam.esm.model.dto.ValidationConstraints.MIN_PAGE_NUMBER;
import static com.epam.esm.model.dto.ValidationConstraints.MIN_PAGE_SIZE;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;
	private final OrderService orderService;

	@GetMapping
	public List<UserDTO> allUsers(@RequestParam(defaultValue = MIN_PAGE_NUMBER + "")
			                              int pageNumber,
	                              @RequestParam(defaultValue = MIN_PAGE_SIZE + "")
			                              int pageSize) {
		PageDTO pageDTO = PageDTO.builder()
				.pageNumber(pageNumber)
				.pageSize(pageSize)
				.build();
		return userService.getAllUsers(pageDTO).getPage();
	}

	@GetMapping("/{id}")
	public UserDTO getUser(@PathVariable("id") int id) {
		return userService.getUser(id);
	}

	@GetMapping("/{id}/orders")
	public List<OrderDTO> getOrdersByUser(@PathVariable("id") int id,
	                                      @RequestParam(defaultValue = MIN_PAGE_NUMBER + "")
			                                      int pageNumber,
	                                      @RequestParam(defaultValue = MIN_PAGE_SIZE + "")
			                                      int pageSize) {
		PageDTO pageDTO = PageDTO.builder()
				.pageNumber(pageNumber)
				.pageSize(pageSize)
				.build();
		return orderService.getOrdersByUser(id, pageDTO).getPage();
	}

}
