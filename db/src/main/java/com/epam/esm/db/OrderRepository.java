package com.epam.esm.db;

import com.epam.esm.model.entity.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.Repository;

import java.util.Optional;

/**
 * Interface for managing Order objects in database
 */
public interface OrderRepository extends Repository<Order, Integer> {
	/**
	 * Creates new order in database (id and purchaseDate properties are ignored).
	 *
	 * @param order object containing source object for new order, id & purchaseDate fields are ignored
	 * @return created order
	 */
	Order save(Order order);

	/**
	 * Retrieves order with given id
	 *
	 * @param id id of order to be retrieved
	 * @return {@link Optional} of order containing corresponding order object, if order with such id exists in database;
	 * * empty {@link Optional} otherwise
	 */
	Optional<Order> findById(Integer id);

	/**
	 * Retrieves all orders in database
	 *
	 * @param pageable paging info
	 * @return paged list of orders
	 */
	Slice<Order> getAllOrdersBy(Pageable pageable);

	/**
	 * Retrieves all orders purchased by specified user
	 *
	 * @param userId   id of user to retrieve purchased orders
	 * @param pageable paging info
	 * @return paged list of orders purchased by given user
	 */
	Slice<Order> getOrdersByUserId(int userId, Pageable pageable);
}
