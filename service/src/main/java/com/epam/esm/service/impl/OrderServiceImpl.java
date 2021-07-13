package com.epam.esm.service.impl;

import com.epam.esm.db.OrderRepository;
import com.epam.esm.model.dto.GiftCertificateOutputDTO;
import com.epam.esm.model.dto.OrderDTO;
import com.epam.esm.model.dto.PageDTO;
import com.epam.esm.model.dto.PagedResultDTO;
import com.epam.esm.model.entity.Order;
import com.epam.esm.model.entity.PagedResult;
import com.epam.esm.service.GiftCertificateService;
import com.epam.esm.service.OrderService;
import com.epam.esm.service.converter.OrderConverter;
import com.epam.esm.service.converter.PagedResultConverter;
import com.epam.esm.service.exception.InvalidOrderException;
import com.epam.esm.service.exception.InvalidUserException;
import com.epam.esm.service.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.function.Supplier;

import static org.springframework.transaction.annotation.Isolation.REPEATABLE_READ;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

	@Value("${order.exception.not-found}")
	private String orderNotFoundExceptionTemplate;
	@Value("${user.exception.not-found}")
	private String userNotFoundExceptionTemplate;

	private final GiftCertificateService giftCertificateService;
	private final OrderConverter orderConverter;
	private final OrderRepository orderRepository;
	private final PagedResultConverter pagedResultConverter;

	@Transactional(isolation = REPEATABLE_READ)
	public OrderDTO createOrder(OrderDTO dto) {
		GiftCertificateOutputDTO certDto = giftCertificateService.getCertificate(dto.getGiftCertificateId());
		Order order = orderConverter.convert(dto, certDto.getPrice());
		order.setId(null);
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
		Optional<Order> optionalOrder;
		try {
			optionalOrder = orderRepository.getOrderById(id);
		} catch (DataAccessException ex) {
			throw new ServiceException(ex);
		}
		return optionalOrder.map(orderConverter::convert)
				.orElseThrow(() -> {
					String identifier = "id=" + id;
					String message = String.format(orderNotFoundExceptionTemplate, identifier);
					return new InvalidOrderException(message, InvalidOrderException.Reason.NOT_FOUND, id);
				});
	}

	private PagedResultDTO<OrderDTO> getOrderDTOList(Supplier<PagedResult<Order>> orderListSupplier) {
		PagedResult<Order> pagedResult;
		try {
			pagedResult = orderListSupplier.get();
		} catch (DataAccessException ex) {
			throw new ServiceException(ex);
		}
		return pagedResultConverter.convertToOrderPage(pagedResult);
	}

	@Override
	public PagedResultDTO<OrderDTO> getAllOrders(PageDTO pageDTO) {
		return getOrderDTOList(() -> orderRepository.getAllOrders(pageDTO.getOffset(), pageDTO.getPageSize()));
	}

	@Override
	public PagedResultDTO<OrderDTO> getOrdersByUser(int userId, PageDTO pageDTO) {
		return getOrderDTOList(
				() -> orderRepository.getOrdersByUserId(userId, pageDTO.getOffset(), pageDTO.getPageSize()));
	}
}
