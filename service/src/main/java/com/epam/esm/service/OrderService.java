package com.epam.esm.service;

import com.epam.esm.model.dto.OrderDTO;
import com.epam.esm.model.entity.Order;

import java.util.List;
import java.util.Optional;

public interface OrderService {
	OrderDTO createOrder(OrderDTO dto);
	OrderDTO getOrder(int id);
	List<OrderDTO> getAllOrders();
	List<OrderDTO> getOrdersByUser(int userId);
}
