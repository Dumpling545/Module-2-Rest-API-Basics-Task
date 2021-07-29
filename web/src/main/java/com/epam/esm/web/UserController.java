package com.epam.esm.web;

import com.epam.esm.model.dto.OrderDTO;
import com.epam.esm.model.dto.PageDTO;
import com.epam.esm.model.dto.PagedResultDTO;
import com.epam.esm.model.dto.UserDTO;
import com.epam.esm.service.OrderService;
import com.epam.esm.service.UserService;
import com.epam.esm.web.assembler.ExtendedRepresentationModelAssembler;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.epam.esm.model.dto.ValidationConstraints.MIN_PAGE_NUMBER;
import static com.epam.esm.model.dto.ValidationConstraints.MIN_PAGE_SIZE;

/**
 * Controller handling requests to 'user' resource
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;
	private final OrderService orderService;
	private final ExtendedRepresentationModelAssembler<UserDTO, UserController> userAssembler;
	private final ExtendedRepresentationModelAssembler<OrderDTO, UserController> orderAssembler;

	@GetMapping
	public ResponseEntity<CollectionModel> allUsers(@RequestParam(defaultValue = MIN_PAGE_NUMBER + "")
			                                                Integer pageNumber,
	                                                @RequestParam(defaultValue = MIN_PAGE_SIZE + "")
			                                                Integer pageSize) {
		PageDTO pageDTO = PageDTO.builder()
				.pageNumber(pageNumber)
				.pageSize(pageSize)
				.build();
		PagedResultDTO<UserDTO> pagedResultDTO = userService.getAllUsers(pageDTO);
		CollectionModel<EntityModel<UserDTO>> model =
				userAssembler.toPagedCollectionModel(pageNumber, pagedResultDTO,
				                                     (c, p) -> c.allUsers(p, pageSize));
		return ResponseEntity.ok(model);
	}

	@GetMapping("/{id}")
	public ResponseEntity<EntityModel> getUser(@PathVariable("id") Integer id) {
		return ResponseEntity.ok(userAssembler.toModel(userService.getUser(id)));
	}

	@GetMapping("/{id}/orders")
	public ResponseEntity<CollectionModel> getOrdersByUser(@PathVariable("id") Integer id,
	                                                       @RequestParam(defaultValue = MIN_PAGE_NUMBER + "")
			                                                       Integer pageNumber,
	                                                       @RequestParam(defaultValue = MIN_PAGE_SIZE + "")
			                                                       Integer pageSize) {
		PageDTO pageDTO = PageDTO.builder()
				.pageNumber(pageNumber)
				.pageSize(pageSize)
				.build();
		PagedResultDTO<OrderDTO> pagedResultDTO = orderService.getOrdersByUser(id, pageDTO);
		CollectionModel<EntityModel<OrderDTO>> model =
				orderAssembler.toPagedCollectionModel(pageNumber, pagedResultDTO,
				                                      (c, p) -> c.getOrdersByUser(id, p, pageSize));
		return ResponseEntity.ok(model);
	}

}
