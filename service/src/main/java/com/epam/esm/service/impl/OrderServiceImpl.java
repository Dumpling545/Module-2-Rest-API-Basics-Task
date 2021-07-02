package com.epam.esm.service.impl;

import com.epam.esm.db.GiftCertificateRepository;
import com.epam.esm.db.OrderRepository;
import com.epam.esm.model.dto.GiftCertificateOutputDTO;
import com.epam.esm.model.dto.OrderDTO;
import com.epam.esm.model.dto.TagDTO;
import com.epam.esm.model.entity.GiftCertificate;
import com.epam.esm.model.entity.Order;
import com.epam.esm.model.entity.Tag;
import com.epam.esm.service.GiftCertificateService;
import com.epam.esm.service.OrderService;
import com.epam.esm.service.TagService;
import com.epam.esm.service.converter.GiftCertificateConverter;
import com.epam.esm.service.converter.OrderConverter;
import com.epam.esm.service.exception.InvalidCertificateException;
import com.epam.esm.service.exception.InvalidOrderException;
import com.epam.esm.service.exception.InvalidTagException;
import com.epam.esm.service.exception.InvalidUserException;
import com.epam.esm.service.exception.ServiceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static org.springframework.transaction.annotation.Isolation.REPEATABLE_READ;

@Service
public class OrderServiceImpl implements OrderService {

	private OrderRepository orderRepository;
	private GiftCertificateService giftCertificateService;
	@Value("${order.exception.not-found}")
	private String orderNotFoundExceptionTemplate;
	@Value("${user.exception.not-found}")
	private String userNotFoundExceptionTemplate;
	private OrderConverter orderConverter;

	public OrderServiceImpl(GiftCertificateService giftCertificateService, OrderRepository orderRepository, OrderConverter orderConverter) {
		this.giftCertificateService = giftCertificateService;
		this.orderRepository = orderRepository;
		this.orderConverter = orderConverter;
	}

	@Transactional(isolation = REPEATABLE_READ)
	public OrderDTO createOrder(OrderDTO dto) {
		GiftCertificateOutputDTO certDto = giftCertificateService.getCertificate(dto.getId());
		Order order = orderConverter.convert(dto, certDto.getPrice());
		try {
			Order newOrder = orderRepository.createOrder(order);
			return orderConverter.convert(newOrder);
		} catch (DataIntegrityViolationException ex) {
			String message = String.format(userNotFoundExceptionTemplate, dto.getUserId());
			throw new InvalidUserException(message, ex, InvalidUserException.Reason.NOT_FOUND, dto.getUserId());
		} catch (DataAccessException ex) {
			throw new ServiceException(ex);
		}
	}

	@Override
	public OrderDTO getOrder(int id) {
		Optional<Order> optionalOrder = Optional.empty();
		try {
			optionalOrder = orderRepository.getOrderById(id);
		} catch (DataAccessException ex) {
			throw new ServiceException(ex);
		}
		OrderDTO dto = optionalOrder.map(orderConverter::convert)
				.orElseThrow(() -> {
					String identifier = "id=" + id;
					String message = String.format(orderNotFoundExceptionTemplate, identifier);
					return new InvalidOrderException(message, InvalidOrderException.Reason.NOT_FOUND, id);
				});
		return dto;
	}
	private List<OrderDTO> getOrderDTOList(Supplier<List<Order>> orderListSupplier){
		List<Order> orderList = Collections.EMPTY_LIST;
		try {
			orderList = orderListSupplier.get();
		} catch (DataAccessException ex) {
			throw new ServiceException(ex);
		}
		List<OrderDTO> dtoList = orderList.stream().map(orderConverter::convert).toList();
		return dtoList;
	}

	@Override
	public List<OrderDTO> getAllOrders() {
		return getOrderDTOList(orderRepository::getAllOrders);
	}

	@Override
	public List<OrderDTO> getOrdersByUser(int userId) {
		return getOrderDTOList(()->orderRepository.getOrdersByUserId(userId));
	}
}
