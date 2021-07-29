package com.epam.esm.db.impl;

import com.epam.esm.db.OrderRepository;
import com.epam.esm.db.helper.FetchQueryHelper;
import com.epam.esm.db.helper.TriConsumer;
import com.epam.esm.model.entity.Order;
import com.epam.esm.model.entity.Order_;
import com.epam.esm.model.entity.PagedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaOrderRepository implements OrderRepository {
	private final FetchQueryHelper fetchQueryHelper;
	@PersistenceContext
	private EntityManager entityManager;

	@Transactional
	public Order createOrder(Order order) {
		LocalDateTime localDateTime = LocalDateTime.now(ZoneOffset.UTC);
		order.setPurchaseDate(localDateTime);
		entityManager.persist(order);
		entityManager.flush();
		entityManager.clear();
		return order;
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Order> getOrderById(int id) {
		Order order = entityManager.find(Order.class, id);
		entityManager.clear();
		return Optional.ofNullable(order);
	}

	@Override
	@Transactional(readOnly = true)
	public PagedResult<Order> getAllOrders(int offset, int limit) {
		TriConsumer<CriteriaBuilder, CriteriaQuery, Root<Order>> queryConfigurator = (cb, cq, r) -> {
			cq.select(r);
		};
		return fetchQueryHelper.fetchPagedResult(Order.class, entityManager, queryConfigurator, offset, limit);
	}

	@Override
	@Transactional(readOnly = true)
	public PagedResult<Order> getOrdersByUserId(int userId, int offset, int limit) {
		TriConsumer<CriteriaBuilder, CriteriaQuery, Root<Order>> queryConfigurator = (cb, cq, r) -> {
			cq.select(r).where(cb.equal(r.get(Order_.userId), userId));
		};
		return fetchQueryHelper.fetchPagedResult(Order.class, entityManager, queryConfigurator, offset, limit);
	}

}
