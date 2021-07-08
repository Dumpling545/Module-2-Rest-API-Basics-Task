package com.epam.esm.web;

import com.epam.esm.model.dto.OrderDTO;
import com.epam.esm.model.dto.PageDTO;
import com.epam.esm.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
import java.util.List;

import static com.epam.esm.model.dto.ValidationConstraints.MIN_PAGE_NUMBER;
import static com.epam.esm.model.dto.ValidationConstraints.MIN_PAGE_SIZE;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
	private final OrderService orderService;

	@GetMapping("/{id}")
	public OrderDTO getOrder(@PathVariable("id") int id) {
		return orderService.getOrder(id);
	}

	@GetMapping
	public List<OrderDTO> allOrders(@RequestParam(defaultValue = MIN_PAGE_NUMBER + "")
			                                int pageNumber,
	                                @RequestParam(defaultValue = MIN_PAGE_SIZE + "")
			                                int pageSize) {
		PageDTO pageDTO = PageDTO.builder()
				.pageNumber(pageNumber)
				.pageSize(pageSize)
				.build();
		return orderService.getAllOrders(pageDTO).getPage();
	}

	@PostMapping
	public ResponseEntity<Object> createOrder(@RequestBody @Valid OrderDTO orderDTO, UriComponentsBuilder ucb) {
		OrderDTO dto = orderService.createOrder(orderDTO);
		HttpHeaders headers = new HttpHeaders();
		URI locationUri = ucb.path("/orders/").path(String.valueOf(dto.getId())).build().toUri();
		headers.setLocation(locationUri);
		ResponseEntity<Object> entity = new ResponseEntity<Object>(headers, HttpStatus.CREATED);
		return entity;
	}
}
