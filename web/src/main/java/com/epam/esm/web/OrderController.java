package com.epam.esm.web;

import com.epam.esm.model.dto.OrderDTO;
import com.epam.esm.model.dto.PageDTO;
import com.epam.esm.model.dto.PagedResultDTO;
import com.epam.esm.service.OrderService;
import com.epam.esm.web.assembler.ExtendedRepresentationModelAssembler;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

import static com.epam.esm.model.dto.ValidationConstraints.MIN_PAGE_NUMBER;
import static com.epam.esm.model.dto.ValidationConstraints.MIN_PAGE_SIZE;

/**
 * Controller handling requests to 'order' resource
 */
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
	private final OrderService orderService;
	private final ExtendedRepresentationModelAssembler<OrderDTO, OrderController> assembler;

	@GetMapping("/{id}")
	public ResponseEntity<EntityModel> getOrder(@PathVariable("id") Integer id) {
		return ResponseEntity.ok(assembler.toModel(orderService.getOrder(id)));
	}

	@GetMapping
	public ResponseEntity<CollectionModel> allOrders(@RequestParam(defaultValue = MIN_PAGE_NUMBER + "")
			                                                 Integer pageNumber,
	                                                 @RequestParam(defaultValue = MIN_PAGE_SIZE + "")
			                                                 Integer pageSize) {
		PageDTO pageDTO = PageDTO.builder()
				.pageNumber(pageNumber)
				.pageSize(pageSize)
				.build();
		PagedResultDTO<OrderDTO> pagedResultDTO = orderService.getAllOrders(pageDTO);
		CollectionModel<EntityModel<OrderDTO>> model =
				assembler.toPagedCollectionModel(pageNumber, pagedResultDTO,
				                                 (c, p) -> c.allOrders(p, pageSize));
		return ResponseEntity.ok(model);
	}

	@PostMapping
	public ResponseEntity createOrder(@RequestBody @Valid OrderDTO orderDTO, UriComponentsBuilder ucb) {
		OrderDTO dto = orderService.createOrder(orderDTO);
		URI locationUri = ucb.path("/orders/").path(String.valueOf(dto.getId())).build().toUri();
		return ResponseEntity.created(locationUri).build();
	}
}
