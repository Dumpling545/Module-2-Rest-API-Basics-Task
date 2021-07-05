package com.epam.esm.db.impl;

import com.epam.esm.db.UserRepository;
import com.epam.esm.db.helper.DatabaseHelper;
import com.epam.esm.db.helper.TriConsumer;
import com.epam.esm.model.entity.Tag;
import com.epam.esm.model.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Repository
@RequiredArgsConstructor
public class JpaUserRepository implements UserRepository {

	@PersistenceContext
	private EntityManager entityManager;

	private final DatabaseHelper databaseHelper;

	@Override
	public Optional<User> getUserById(int id) {
		User user = entityManager.find(User.class, id);
		entityManager.clear();
		return Optional.ofNullable(user);
	}

	@Override
	public List<User> getAllUsers() {
		TriConsumer<CriteriaBuilder, CriteriaQuery<User>, Root<User>> queryConfigurator = (cb, cq, r) -> {
			cq.select(r);
		};
		Function<TypedQuery<User>, List<User>> resultProducer = TypedQuery::getResultList;
		return databaseHelper.execute(User.class, entityManager, queryConfigurator, resultProducer);
	}
}
