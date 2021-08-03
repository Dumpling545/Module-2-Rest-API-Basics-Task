package com.epam.esm.web;

import com.epam.esm.model.dto.OrderDTO;
import com.epam.esm.model.dto.UserDTO;
import com.epam.esm.service.OrderService;
import com.epam.esm.service.UserService;
import com.epam.esm.web.assembler.ExtendedRepresentationModelAssembler;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

import static com.epam.esm.web.ResourcePaths.USERS;

/**
 * Controller handling requests to 'user' resource
 */
@RestController
@RequestMapping(USERS)
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;
	private final OrderService orderService;
	private final ExtendedRepresentationModelAssembler<UserDTO> userAssembler;
	private final ExtendedRepresentationModelAssembler<OrderDTO> orderAssembler;

	@GetMapping
	public ResponseEntity<CollectionModel> allUsers(Pageable pageable) {
		Slice<UserDTO> slice = userService.getAllUsers(pageable);
		CollectionModel<EntityModel<UserDTO>> model = userAssembler.toSliceModel(slice);
		return ResponseEntity.ok(model);
	}

	@GetMapping("/{id}")
	public ResponseEntity<EntityModel> getUser(@PathVariable("id") Integer id) {
		return ResponseEntity.ok(userAssembler.toModel(userService.getUser(id)));
	}

	@GetMapping("/{id}/orders")
	public ResponseEntity<CollectionModel> getOrdersByUser(@PathVariable("id") Integer id, Pageable pageable) {
		Slice<OrderDTO> slice = orderService.getOrdersByUser(id, pageable);
		CollectionModel<EntityModel<OrderDTO>> model = orderAssembler.toSliceModel(slice);
		return ResponseEntity.ok(model);
	}

	@PostMapping("/register")
	public ResponseEntity registerUser(@RequestBody @Valid UserDTO userDTO, UriComponentsBuilder ucb) {
		UserDTO dto = userService.registerUser(userDTO);
		URI locationUri = ucb.path(USERS + "/").path(String.valueOf(dto.getId())).build().toUri();
		return ResponseEntity.created(locationUri).build();
	}

}
