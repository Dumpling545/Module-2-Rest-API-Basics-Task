package com.epam.esm.db;

import com.epam.esm.model.entity.Order;
import com.epam.esm.model.entity.PagedResult;

import java.util.Optional;

public interface OrderRepository {
	Order createOrder(Order order);
	Optional<Order> getOrderById(int id);
	PagedResult<Order> getAllOrders(int offset, int limit);
	PagedResult<Order> getOrdersByUserId(int userId, int offset, int limit);
}
