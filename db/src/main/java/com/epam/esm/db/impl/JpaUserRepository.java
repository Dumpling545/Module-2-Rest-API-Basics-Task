package com.epam.esm.db.impl;

import com.epam.esm.db.UserRepository;
import com.epam.esm.db.helper.DatabaseHelper;
import com.epam.esm.db.helper.TriConsumer;
import com.epam.esm.model.entity.PagedResult;
import com.epam.esm.model.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaUserRepository implements UserRepository {

	@PersistenceContext
	private EntityManager entityManager;

	private final DatabaseHelper databaseHelper;

	@Override
	@Transactional(readOnly = true)
	public Optional<User> getUserById(int id) {
		User user = entityManager.find(User.class, id);
		entityManager.clear();
		return Optional.ofNullable(user);
	}

	@Override
	@Transactional(readOnly = true)
	public PagedResult<User> getAllUsers(int offset, int limit) {
		TriConsumer<CriteriaBuilder, CriteriaQuery, Root<User>> queryConfigurator = (cb, cq, r) -> {
			cq.select(r);
		};
		return databaseHelper.fetchPagedResult(User.class, entityManager, queryConfigurator, offset, limit);
	}

}
