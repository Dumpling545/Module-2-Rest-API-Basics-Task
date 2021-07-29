package com.epam.esm.db;

import com.epam.esm.model.entity.Order;
import com.epam.esm.model.entity.PagedResult;

import java.util.Optional;

/**
 * Interface for managing Order objects in database
 */
public interface OrderRepository {
    /**
     * Creates new order in database (id and purchaseDate properties are ignored).
     *
     * @param order object containing source object for new order, id & purchaseDate fields are ignored
     * @return created order
     */
    Order createOrder(Order order);

    /**
     * Retrieves order with given id
     *
     * @param id id of order to be retrieved
     * @return {@link Optional} of order containing corresponding order object, if order with such id exists in database;
     * * empty {@link Optional} otherwise
     */
    Optional<Order> getOrderById(int id);

    /**
     * Retrieves all orders in database
     *
     * @param offset how many elements to skip
     * @param limit  how many elements to retrieve
     * @return paged list of orders
     */
    PagedResult<Order> getAllOrders(int offset, int limit);

    /**
     * Retrieves all orders purchased by specified user
     *
     * @param userId id of user to retrieve purchased orders
     * @param offset how many elements to skip
     * @param limit  how many elements to retrieve
     * @return paged list of orders purchased by given user
     */
    PagedResult<Order> getOrdersByUserId(int userId, int offset, int limit);
}
