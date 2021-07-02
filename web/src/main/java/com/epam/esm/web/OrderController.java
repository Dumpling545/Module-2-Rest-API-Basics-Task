package com.epam.esm.web;

import com.epam.esm.model.dto.OrderDTO;
import com.epam.esm.model.dto.TagDTO;
import com.epam.esm.model.dto.UserDTO;
import com.epam.esm.service.OrderService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {
	private OrderService orderService;

	public OrderController(OrderService orderService) {
		this.orderService = orderService;
	}
	@GetMapping("/{id}")
	public OrderDTO getOrder(@PathVariable("id") int id) {
		return orderService.getOrder(id);
	}

	@GetMapping
	public List<OrderDTO> allOrders() {
		return orderService.getAllOrders();
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
