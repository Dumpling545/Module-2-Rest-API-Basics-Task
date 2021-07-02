package com.epam.esm.db;

import com.epam.esm.model.entity.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {
	Order createOrder(Order order);
	Optional<Order> getOrderById(int id);
	List<Order> getAllOrders();
	List<Order> getOrdersByUserId(int userId);
}
