package com.epam.esm.db.helper;

import com.epam.esm.model.entity.PagedResult;
import org.springframework.stereotype.Component;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.function.Function;

/**
 * Helper class performing fetch queries aimed to reduce JPA boilerplate code
 */
@Component
public class FetchQueryHelper {

	public <Q, O> O fetch(Class<Q> queryClass, EntityManager entityManager,
	                      TriConsumer<CriteriaBuilder, CriteriaQuery, Root<Q>> queryConfigurator,
	                      Function<TypedQuery<Q>, O> resultProducer) {
		return fetch(queryClass, entityManager, queryConfigurator, null, resultProducer);
	}

	public <Q, O> O fetch(Class<Q> queryClass, EntityManager entityManager,
	                      TriConsumer<CriteriaBuilder, CriteriaQuery, Root<Q>> queryConfigurator,
	                      Function<EntityManager, EntityGraph<Q>> entityGraphProducer,
	                      Function<TypedQuery<Q>, O> resultProducer) {
		return fetch(queryClass, queryClass, entityManager, queryConfigurator, entityGraphProducer, resultProducer,
		             true);
	}

	public <Q, R, O> O fetch(Class<Q> queryClass, Class<R> rootClass, EntityManager entityManager,
	                         TriConsumer<CriteriaBuilder, CriteriaQuery, Root<R>> queryConfigurator,
	                         Function<TypedQuery<Q>, O> outputProducer, boolean clearContext) {
		return fetch(queryClass, rootClass, entityManager, queryConfigurator, null, outputProducer, clearContext);
	}

	public <Q, R, O> O fetch(Class<Q> queryClass, Class<R> rootClass, EntityManager entityManager,
	                         TriConsumer<CriteriaBuilder, CriteriaQuery, Root<R>> queryConfigurator,
	                         Function<EntityManager, EntityGraph<Q>> entityGraphProducer,
	                         Function<TypedQuery<Q>, O> outputProducer, boolean clearContext) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Q> criteriaQuery = criteriaBuilder.createQuery(queryClass);
		Root<R> root = criteriaQuery.from(rootClass);
		queryConfigurator.accept(criteriaBuilder, criteriaQuery, root);
		TypedQuery<Q> typedQuery = entityManager.createQuery(criteriaQuery);
		if (entityGraphProducer != null) {
			typedQuery.setHint("javax.persistence.loadgraph", entityGraphProducer.apply(entityManager));
		}
		O result = outputProducer.apply(typedQuery);
		if (clearContext) {
			entityManager.clear();
		}
		return result;
	}

	public <Q, R> PagedResult<Q> fetchPagedResult(Class<Q> queryClass, Class<R> rootClass,
	                                              EntityManager entityManager,
	                                              TriConsumer<CriteriaBuilder, CriteriaQuery, Root<R>> queryConfigurator,
	                                              Function<EntityManager, EntityGraph<Q>> entityGraphProducer,
	                                              int offset, int limit, boolean clearContext) {
		List<Q> result = fetch(queryClass, rootClass, entityManager, queryConfigurator, entityGraphProducer,
		                       (tq) -> tq.setFirstResult(offset).setMaxResults(limit + 1).getResultList(), false);
		return PagedResult.<Q>builder()
				.first(offset == 0)
				.last(result.size() <= limit)
				.page(result.subList(0, Math.min(limit, result.size()))).build();
	}

	public <Q, R> PagedResult<Q> fetchPagedResult(Class<Q> queryClass, Class<R> rootClass,
	                                              EntityManager entityManager,
	                                              TriConsumer<CriteriaBuilder, CriteriaQuery, Root<R>> queryConfigurator,
	                                              Function<EntityManager, EntityGraph<Q>> entityGraphProducer,
	                                              int offset, int limit) {
		return fetchPagedResult(queryClass, rootClass, entityManager, queryConfigurator, entityGraphProducer,
		                        offset, limit, true);
	}

	public <Q, R> PagedResult<Q> fetchPagedResult(Class<Q> queryClass, Class<R> rootClass,
	                                              EntityManager entityManager,
	                                              TriConsumer<CriteriaBuilder, CriteriaQuery, Root<R>> queryConfigurator,
	                                              int offset, int limit, boolean clearContext) {
		return fetchPagedResult(queryClass, rootClass, entityManager, queryConfigurator, null,
		                        offset, limit, clearContext);
	}

	public <Q, R> PagedResult<Q> fetchPagedResult(Class<Q> queryClass, Class<R> rootClass,
	                                              EntityManager entityManager,
	                                              TriConsumer<CriteriaBuilder, CriteriaQuery, Root<R>> queryConfigurator,
	                                              int offset, int limit) {
		return fetchPagedResult(queryClass, rootClass, entityManager, queryConfigurator, offset, limit, true);
	}


	public <Q> PagedResult<Q> fetchPagedResult(Class<Q> rootClass,
	                                           EntityManager entityManager,
	                                           TriConsumer<CriteriaBuilder, CriteriaQuery, Root<Q>> queryConfigurator,
	                                           Function<EntityManager, EntityGraph<Q>> entityGraphProducer,
	                                           int offset, int limit) {
		return fetchPagedResult(rootClass, rootClass, entityManager, queryConfigurator, entityGraphProducer, offset,
		                        limit);
	}

	public <Q> PagedResult<Q> fetchPagedResult(Class<Q> queryClass,
	                                           EntityManager entityManager,
	                                           TriConsumer<CriteriaBuilder, CriteriaQuery, Root<Q>> queryConfigurator,
	                                           int offset, int limit) {
		return fetchPagedResult(queryClass, queryClass, entityManager, queryConfigurator, offset, limit, true);
	}
}
