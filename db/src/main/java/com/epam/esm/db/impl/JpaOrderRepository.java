package com.epam.esm.db.impl;

import com.epam.esm.db.OrderRepository;
import com.epam.esm.db.helper.DatabaseHelper;
import com.epam.esm.db.helper.TriConsumer;
import com.epam.esm.model.entity.Order;
import com.epam.esm.model.entity.Order_;
import com.epam.esm.model.entity.Tag;
import com.epam.esm.model.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Repository
@RequiredArgsConstructor

public class JpaOrderRepository implements OrderRepository {
	@PersistenceContext
	private EntityManager entityManager;

	private final DatabaseHelper databaseHelper;

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
	public Optional<Order> getOrderById(int id) {
		Order order = entityManager.find(Order.class, id);
		entityManager.clear();
		return Optional.ofNullable(order);
	}

	@Override
	public List<Order> getAllOrders() {
		TriConsumer<CriteriaBuilder, CriteriaQuery<Order>, Root<Order>> queryConfigurator = (cb, cq, r) -> {
			cq.select(r);
		};
		Function<TypedQuery<Order>, List<Order>> resultProducer = TypedQuery::getResultList;
		return databaseHelper.execute(Order.class, entityManager, queryConfigurator, resultProducer);
	}

	@Override
	public List<Order> getOrdersByUserId(int userId) {
		TriConsumer<CriteriaBuilder, CriteriaQuery<Order>, Root<Order>> queryConfigurator = (cb, cq, r) -> {
			cq.select(r).where(cb.equal(r.get(Order_.userId), userId));
		};
		Function<TypedQuery<Order>, List<Order>> resultProducer = TypedQuery::getResultList;
		return databaseHelper.execute(Order.class, entityManager, queryConfigurator, resultProducer);
	}
}
