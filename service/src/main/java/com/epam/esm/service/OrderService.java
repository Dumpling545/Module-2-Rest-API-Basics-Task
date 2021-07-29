package com.epam.esm.service;

import com.epam.esm.model.dto.OrderDTO;
import com.epam.esm.model.dto.PageDTO;
import com.epam.esm.model.dto.PagedResultDTO;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

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
     * @param page paging info
     * @return paged list of orders
     */
    PagedResultDTO<OrderDTO> getAllOrders(@Valid PageDTO page);

    /**
     * Retrieves all existing orders purchased by specified user
     *
     * @param userId user's id
     * @param page   paging info
     * @return paged list of orders
     */
    PagedResultDTO<OrderDTO> getOrdersByUser(int userId, @Valid PageDTO page);
}
