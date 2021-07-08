package com.epam.esm.service;

import com.epam.esm.model.dto.OrderDTO;
import com.epam.esm.model.dto.PageDTO;
import com.epam.esm.model.dto.PagedResultDTO;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

@Validated
public interface OrderService {
	OrderDTO createOrder(OrderDTO dto);
	OrderDTO getOrder(int id);
	PagedResultDTO<OrderDTO> getAllOrders(@Valid PageDTO page);
	PagedResultDTO<OrderDTO> getOrdersByUser(int userId, @Valid PageDTO page);
}
