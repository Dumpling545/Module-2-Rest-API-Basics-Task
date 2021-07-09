package com.epam.esm.db;

import com.epam.esm.GiftCertificateSystemApplication;
import com.epam.esm.model.entity.Order;
import com.epam.esm.model.entity.PagedResult;
import com.epam.esm.model.entity.Order;
import com.epam.esm.model.entity.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith({SpringExtension.class})
@SpringBootTest(classes = GiftCertificateSystemApplication.class)
@AutoConfigureTestDatabase
public class OrderRepositoryTest {
	private static final Order nonExistingOrder1 = Order.builder()
			.id(-1)
			.userId(1)
			.giftCertificateId(1)
			.cost(BigDecimal.valueOf(1.12))
			.purchaseDate(LocalDateTime.now()).build();
	private static final Order existingOrder1 = Order.builder()
			.id(1)
			.userId(1)
			.giftCertificateId(3)
			.cost(BigDecimal.valueOf(1.1))
			.purchaseDate(LocalDate.parse("2018-01-01").atStartOfDay()).build();
	private static final Order orderToBeCreated = Order.builder()
			.userId(3)
			.giftCertificateId(7)
			.cost(BigDecimal.valueOf(1.13)).build();
	private static final int ALL_ORDERS_EXISTING_OFFSET = 0;
	private static final int ALL_ORDERS_EXISTING_LIMIT = 5;
	private static final int ALL_ORDERS_NON_EXISTING_OFFSET = 100;
	private static final int ALL_ORDERS_NON_EXISTING_LIMIT = 5;
	private static final int NON_EXISTENT_USER_ID = 12345;
	@Autowired
	OrderRepository orderRepository;

	private void assertOrdersEqual(Order o1, Order o2){
		assertEquals(o1.getId(), o2.getId());
		assertEquals(o1.getUserId(), o2.getUserId());
		assertEquals(o1.getGiftCertificateId(), o2.getGiftCertificateId());
		assertEquals(o1.getCost(), o2.getCost());
		if(o1.getPurchaseDate() != null && o2.getPurchaseDate() != null){
			assertEquals(o1.getPurchaseDate().get(ChronoField.MINUTE_OF_DAY),
					o1.getPurchaseDate().get(ChronoField.MINUTE_OF_DAY));
		}
	}
	@Test
	public void createOrderShouldReturnOrderWhenPassedCorrectOrder() {
		Order created = orderRepository.createOrder(orderToBeCreated);
		assertNotNull(created);
		Optional<Order> fetchedAfterCreation = orderRepository.getOrderById(created.getId());
		assertTrue(fetchedAfterCreation.isPresent());
		assertOrdersEqual(fetchedAfterCreation.get(), created);
	}
	@Test
	public void getOrderByIdShouldReturnOptionalWithOrderWhenPassedExistingOrderId() {
		Optional<Order> optional = orderRepository.getOrderById(existingOrder1.getId());
		assertTrue(optional.isPresent());
		assertOrdersEqual(existingOrder1, optional.get());
	}

	@Test
	public void getOrderByIdShouldReturnEmptyOptionalWhenPassedNonExistingOrderId() {
		Optional<Order> optional = orderRepository.getOrderById(nonExistingOrder1.getId());
		assertTrue(optional.isEmpty());
	}

	@Test
	public void getAllOrdersShouldReturnNonEmptyListWhenPassedExistingOffsetAndLimit() {
		PagedResult<Order> orders = orderRepository.getAllOrders(ALL_ORDERS_EXISTING_OFFSET, ALL_ORDERS_EXISTING_LIMIT);
		assertFalse(orders.getPage().isEmpty());
	}

	@Test
	public void getAllOrdersShouldReturnEmptyListWhenPassedNonExistingOffsetAndLimit() {
		PagedResult<Order> orders = orderRepository.getAllOrders(ALL_ORDERS_NON_EXISTING_OFFSET,
				ALL_ORDERS_NON_EXISTING_LIMIT);
		assertTrue(orders.getPage().isEmpty());
	}

	@Test
	public void getOrdersByUserIdShouldReturnNonEmptyListWhenPassedExistingOffsetAndLimit() {
		PagedResult<Order> orders = orderRepository.getOrdersByUserId(existingOrder1.getUserId(),
				ALL_ORDERS_EXISTING_OFFSET, ALL_ORDERS_EXISTING_LIMIT);
		assertFalse(orders.getPage().isEmpty());
	}

	@Test
	public void getOrdersByUserIdShouldReturnEmptyListWhenPassedNonExistingUserId() {
		PagedResult<Order> orders = orderRepository.getOrdersByUserId(NON_EXISTENT_USER_ID,
				ALL_ORDERS_EXISTING_OFFSET,
				ALL_ORDERS_EXISTING_LIMIT);
		assertTrue(orders.getPage().isEmpty());
	}

	@Test
	public void getOrdersByUserIdShouldReturnEmptyListWhenPassedNonExistingOffsetAndLimit() {
		PagedResult<Order> orders = orderRepository.getOrdersByUserId(existingOrder1.getUserId(),
				ALL_ORDERS_NON_EXISTING_OFFSET,
				ALL_ORDERS_NON_EXISTING_LIMIT);
		assertTrue(orders.getPage().isEmpty());
	}
}
