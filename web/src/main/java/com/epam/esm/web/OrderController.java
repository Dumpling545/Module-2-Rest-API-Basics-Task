package com.epam.esm.web;

import com.epam.esm.model.dto.OrderDTO;
import com.epam.esm.service.OrderService;
import com.epam.esm.web.assembler.ExtendedRepresentationModelAssembler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

import static com.epam.esm.web.ResourcePaths.ORDERS;

/**
 * Controller handling requests to 'order' resource
 */
@RestController
@RequestMapping(ORDERS)
@RequiredArgsConstructor
public class OrderController {
	@Value("${oauth2.claims.user-id}")
	private String userIdClaimName;
	private final OrderService orderService;
	private final ExtendedRepresentationModelAssembler<OrderDTO> assembler;

	@GetMapping("/{id}")
	public ResponseEntity<EntityModel> getOrder(@PathVariable("id") Integer id) {
		return ResponseEntity.ok(assembler.toModel(orderService.getOrder(id)));
	}

	@GetMapping
	public ResponseEntity<CollectionModel> allOrders(Pageable pageable) {
		Slice<OrderDTO> slice = orderService.getAllOrders(pageable);
		CollectionModel<EntityModel<OrderDTO>> model = assembler.toSliceModel(slice);
		return ResponseEntity.ok(model);
	}

	@PostMapping
	@PreAuthorize("hasPermission(#orderDTO, 'CREATE')")
	public ResponseEntity createOrder(Authentication authentication,
	                                  @RequestBody @Valid OrderDTO orderDTO, UriComponentsBuilder ucb) {
		if (orderDTO.getUserId() == null) {
			Jwt jwtToken = (Jwt) authentication.getPrincipal();
			int userId = jwtToken.<Long>getClaim(userIdClaimName).intValue();
			orderDTO.setUserId(userId);
		}
		OrderDTO dto = orderService.createOrder(orderDTO);
		URI locationUri = ucb.path("/orders/").path(String.valueOf(dto.getId())).build().toUri();
		return ResponseEntity.created(locationUri).build();
	}
}
