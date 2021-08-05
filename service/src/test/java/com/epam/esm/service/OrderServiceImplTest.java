package com.epam.esm.service;

import com.epam.esm.db.OrderRepository;
import com.epam.esm.model.dto.GiftCertificateOutputDTO;
import com.epam.esm.model.dto.OrderDTO;
import com.epam.esm.model.entity.Order;
import com.epam.esm.service.converter.OrderConverter;
import com.epam.esm.service.exception.InvalidOrderException;
import com.epam.esm.service.exception.InvalidUserException;
import com.epam.esm.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.data.util.TypeInformation;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.EMPTY_LIST;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OrderServiceImplTest {
	private static final BigDecimal CERT_PRICE = BigDecimal.valueOf(12345.67);
	private static final OrderDTO orderDtoToBeCreated = OrderDTO.builder()
			.userId(100)
			.giftCertificateId(100)
			.cost(BigDecimal.valueOf(1.01))
			.build();
	private static final Order mockedCreatedOrder = Order.builder()
			.id(123)
			.userId(orderDtoToBeCreated.getUserId())
			.giftCertificateId(orderDtoToBeCreated.getGiftCertificateId())
			.cost(CERT_PRICE)
			.purchaseDate(LocalDateTime.now())
			.build();
	private static final OrderDTO existingOrderDto1 = OrderDTO.builder()
			.id(1)
			.userId(1)
			.giftCertificateId(1)
			.cost(BigDecimal.valueOf(120.1))
			.build();
	private static final Order existingOrder1 = Order.builder()
			.id(existingOrderDto1.getId())
			.userId(existingOrderDto1.getUserId())
			.giftCertificateId(existingOrderDto1.getGiftCertificateId())
			.cost(existingOrderDto1.getCost())
			.purchaseDate(existingOrderDto1.getPurchaseDate())
			.build();
	private static final OrderDTO existingOrderDto2 = OrderDTO.builder()
			.id(2)
			.userId(2)
			.giftCertificateId(3)
			.cost(BigDecimal.valueOf(26.15))
			.build();
	private static final Order existingOrder2 = Order.builder()
			.id(existingOrderDto2.getId())
			.userId(existingOrderDto2.getUserId())
			.giftCertificateId(existingOrderDto2.getGiftCertificateId())
			.cost(existingOrderDto2.getCost())
			.purchaseDate(existingOrderDto2.getPurchaseDate())
			.build();
	private static final List<Order> allExistingOrderList = List.of(existingOrder1, existingOrder2);
	private static final Slice<Order> orderSlice = new SliceImpl<>(allExistingOrderList);
	private static final List<OrderDTO> allExistingOrderDtoList = List.of(existingOrderDto1, existingOrderDto2);
	private static final Slice<OrderDTO> orderDtoSlice = new SliceImpl<>(allExistingOrderDtoList);
	private static final OrderDTO nonExistingOrderDto = OrderDTO.builder()
			.id(-10)
			.userId(2)
			.giftCertificateId(3)
			.cost(BigDecimal.valueOf(26.15))
			.build();
	private static final String ORDER_NAME_2 = "order name 2";
	private static final String ALREADY_EXISTS_MESSAGE_FIELD_NAME = "alreadyExistsExceptionTemplate";
	private static final String ORDER_NOT_FOUND_EXCEPTION_TEMPLATE = "orderNotFoundExceptionTemplate";
	private static final String MOCK_EX_MESSAGE = "test";
	private static final int EXISTING_CERT_ID = 1;
	private static final int NON_EXISTING_CERT_ID = 1;
	private static final Pageable pageable = Pageable.ofSize(5);
	private static final String USER_NOT_FOUND_EXCEPTION_TEMPLATE = "userNotFoundExceptionTemplate";
	private static final String INVALID_FIELD_TOKEN_TEMPLATE_FIELD_NAME = "invalidFieldTokenTemplate";
	private static final int USER_ID = 1;
	private final OrderConverter orderConverter = Mappers.getMapper(OrderConverter.class);

	@Test
	public void createOrderShouldReturnNewDtoWithIdWhenPassedCorrectDto() {
		OrderRepository orderRepository = Mockito.mock(OrderRepository.class);
		GiftCertificateService gcService = Mockito.mock(GiftCertificateService.class);
		Mockito.when(gcService.getCertificate(Mockito.anyInt())).thenReturn(GiftCertificateOutputDTO.builder()
				                                                                    .id(orderDtoToBeCreated.getId())
				                                                                    .price(CERT_PRICE).build());
		Mockito.when(orderRepository
				             .save(Mockito.any()))
				.thenReturn(mockedCreatedOrder);
		OrderService orderService = new OrderServiceImpl(gcService, orderConverter, orderRepository);
		assertDoesNotThrow(() -> {
			OrderDTO newDto = orderService.createOrder(orderDtoToBeCreated);
			assertNotSame(orderDtoToBeCreated, newDto);
			assertEquals(mockedCreatedOrder.getId(), newDto.getId());
			assertEquals(mockedCreatedOrder.getCost(), newDto.getCost());
			assertEquals(mockedCreatedOrder.getPurchaseDate(), newDto.getPurchaseDate());});
		}


	@Test
	public void createOrderShouldThrowExceptionWhenPassedNonExistentUserId() {
		OrderRepository orderRepository = Mockito.mock(OrderRepository.class);
		GiftCertificateService gcService = Mockito.mock(GiftCertificateService.class);
		Mockito.when(gcService.getCertificate(Mockito.anyInt())).thenReturn(GiftCertificateOutputDTO.builder()
				                                                                    .id(orderDtoToBeCreated.getId())
				                                                                    .price(CERT_PRICE).build());
		Mockito.when(orderRepository
				             .save(Mockito.any()))
				.thenThrow(DataIntegrityViolationException.class);

		OrderService orderService =
				new OrderServiceImpl(gcService, orderConverter, orderRepository);
		ReflectionTestUtils.setField(orderService, USER_NOT_FOUND_EXCEPTION_TEMPLATE, MOCK_EX_MESSAGE, String.class);
		assertThrows(InvalidUserException.class, () -> orderService.createOrder(orderDtoToBeCreated));
	}

	@Test
	public void getOrderShouldReturnDtoWhenPassedExistingOrderId() {
		OrderRepository orderRepository = Mockito.mock(OrderRepository.class);
		GiftCertificateService gcService = Mockito.mock(GiftCertificateService.class);
		Mockito.when(orderRepository.findById(Mockito.eq(existingOrderDto1.getId())))
				.thenReturn(Optional.of(existingOrder1));
		OrderService orderService = new OrderServiceImpl(gcService, orderConverter, orderRepository);
		assertDoesNotThrow(() -> {
			OrderDTO res = orderService.getOrder(existingOrderDto1.getId());
			assertEquals(existingOrderDto1, res);
		});
	}

	@Test
	public void getOrderShouldThrowExceptionWhenPassedNonExistingOrderId() {
		OrderRepository orderRepository = Mockito.mock(OrderRepository.class);
		GiftCertificateService gcService = Mockito.mock(GiftCertificateService.class);
		Mockito.when(orderRepository.findById(Mockito.anyInt())).thenReturn(Optional.empty());
		OrderService orderService = new OrderServiceImpl(gcService, orderConverter, orderRepository);
		ReflectionTestUtils.setField(orderService, ORDER_NOT_FOUND_EXCEPTION_TEMPLATE, MOCK_EX_MESSAGE, String.class);
		InvalidOrderException ex = assertThrows(InvalidOrderException.class,
		                                        () -> orderService.getOrder(existingOrderDto1.getId()));
		assertEquals(InvalidOrderException.Reason.NOT_FOUND, ex.getReason());
	}

	@Test
	public void getAllOrdersShouldThrowInvalidUserExceptionWhenInnerPropertyReferenceExceptionIsThrown() {
		OrderRepository orderRepository = Mockito.mock(OrderRepository.class);
		GiftCertificateService gcService = Mockito.mock(GiftCertificateService.class);
		OrderService orderService = new OrderServiceImpl(gcService, orderConverter, orderRepository);
		TypeInformation typeInformation = Mockito.mock(TypeInformation.class);
		PropertyReferenceException pre = new PropertyReferenceException(MOCK_EX_MESSAGE, typeInformation, EMPTY_LIST);
		Mockito.when(orderRepository.getAllOrdersBy(Mockito.any())).thenThrow(pre);
		ReflectionTestUtils.setField(orderService, INVALID_FIELD_TOKEN_TEMPLATE_FIELD_NAME, MOCK_EX_MESSAGE,
		                             String.class);
		InvalidOrderException ite = assertThrows(InvalidOrderException.class,
		                                         () -> orderService.getAllOrders(pageable));
		assertEquals(InvalidOrderException.Reason.INVALID_SORT_BY, ite.getReason());
	}

	@Test
	public void getAllOrdersShouldNotThrowExceptionAndReturnSlice() {
		OrderRepository orderRepository = Mockito.mock(OrderRepository.class);
		GiftCertificateService gcService = Mockito.mock(GiftCertificateService.class);
		Mockito.when(orderRepository.getAllOrdersBy(Mockito.eq(pageable)))
				.thenReturn(orderSlice);
		OrderService orderService =
				new OrderServiceImpl(gcService, orderConverter, orderRepository);
		assertDoesNotThrow(() -> {
			Slice<OrderDTO> orderDTOSlice = orderService.getAllOrders(pageable);
			assertEquals(orderDtoSlice, orderDTOSlice);
		});
	}

	@Test
	public void getOrdersByUserShouldNotThrowExceptionAndReturnSlice() {
		OrderRepository orderRepository = Mockito.mock(OrderRepository.class);
		GiftCertificateService gcService = Mockito.mock(GiftCertificateService.class);
		Mockito.when(orderRepository.getOrdersByUserId(Mockito.anyInt(), Mockito.eq(pageable))).thenReturn(orderSlice);
		OrderService orderService = new OrderServiceImpl(gcService, orderConverter, orderRepository);
		assertDoesNotThrow(() -> {
			Slice<OrderDTO> orderDTOSlice = orderService.getOrdersByUser(USER_ID, pageable);
			assertEquals(orderDtoSlice, orderDTOSlice);
		});
	}
}
