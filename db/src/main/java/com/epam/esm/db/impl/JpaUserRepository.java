package com.epam.esm.db.impl;

import com.epam.esm.db.UserRepository;
import com.epam.esm.db.helper.FetchQueryHelper;
import com.epam.esm.db.helper.TriConsumer;
import com.epam.esm.model.entity.PagedResult;
import com.epam.esm.model.entity.User;
import com.epam.esm.model.entity.User_;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Optional;
import java.util.function.Function;

@Repository
@RequiredArgsConstructor
public class JpaUserRepository implements UserRepository {

    private final FetchQueryHelper fetchQueryHelper;
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserById(int id) {
        User user = entityManager.find(User.class, id);
        entityManager.clear();
        return Optional.ofNullable(user);
    }

	@Override
	@Transactional(readOnly = true)
	public Optional<User> getUserByName(String name) {
		TriConsumer<CriteriaBuilder, CriteriaQuery, Root<User>> queryConfigurator = (cb, cq, r) -> {
			cq.select(r).where(cb.equal(r.get(User_.userName), name));
		};
		Function<TypedQuery<User>, Optional<User>> resultProducer = tq -> tq.getResultStream().findFirst();
		return fetchQueryHelper.fetch(User.class, entityManager, queryConfigurator, resultProducer);

	}

    @Override
    @Transactional(readOnly = true)
    public PagedResult<User> getAllUsers(int offset, int limit) {
        TriConsumer<CriteriaBuilder, CriteriaQuery, Root<User>> queryConfigurator = (cb, cq, r) -> {
            cq.select(r);
        };
        return fetchQueryHelper.fetchPagedResult(User.class, entityManager, queryConfigurator, offset, limit);
    }

	@Override
	@Transactional
	public User createUser(User user) {
		entityManager.persist(user);
		entityManager.flush();
		entityManager.clear();
		return user;
	}
}
