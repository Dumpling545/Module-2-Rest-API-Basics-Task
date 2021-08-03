package com.epam.esm.service;

import com.epam.esm.model.dto.OrderDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.validation.annotation.Validated;

/**
 * Interface of service for manipulating Order DTO objects
 */
@Validated
public interface OrderService {
	/**
	 * Creates order object
	 *
	 * @param dto dto representing order to be created, id is ignored
	 * @return dto representing created order
	 */
	OrderDTO createOrder(OrderDTO dto);

	/**
	 * Retrieves order object by given id
	 *
	 * @param id if of order to be retrieved
	 * @return order matching provided id
	 */
	OrderDTO getOrder(int id);

	/**
	 * Retrieves all existing orders from database
	 *
	 * @param pageable paging info
	 * @return paged list of orders
	 */
	Slice<OrderDTO> getAllOrders(Pageable pageable);

	/**
	 * Retrieves all existing orders purchased by specified user
	 *
	 * @param userId   user's id
	 * @param pageable paging info
	 * @return paged list of orders
	 */
	Slice<OrderDTO> getOrdersByUser(int userId, Pageable pageable);
}
