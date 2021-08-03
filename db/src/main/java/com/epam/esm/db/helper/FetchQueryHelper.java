package com.epam.esm.db.helper;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
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

	public <Q, R> Slice<Q> fetchSlice(Class<Q> queryClass, Class<R> rootClass,
	                                  EntityManager entityManager,
	                                  TriConsumer<CriteriaBuilder, CriteriaQuery, Root<R>> queryConfigurator,
	                                  Function<EntityManager, EntityGraph<Q>> entityGraphProducer,
	                                  Pageable pageable, boolean clearContext) {
		List<Q> result = fetch(queryClass, rootClass, entityManager, queryConfigurator, entityGraphProducer,
		                       (tq) -> tq.setFirstResult((int) pageable.getOffset())
				                       .setMaxResults(pageable.getPageSize() + 1).getResultList(), false);
		return new SliceImpl<>(result.subList(0, Math.min(pageable.getPageSize(), result.size())),
		                       pageable, result.size() > pageable.getPageSize());
	}

	public <Q, R> Slice<Q> fetchSlice(Class<Q> queryClass, Class<R> rootClass,
	                                  EntityManager entityManager,
	                                  TriConsumer<CriteriaBuilder, CriteriaQuery, Root<R>> queryConfigurator,
	                                  Function<EntityManager, EntityGraph<Q>> entityGraphProducer,
	                                  Pageable pageable) {
		return fetchSlice(queryClass, rootClass, entityManager, queryConfigurator, entityGraphProducer,
		                  pageable, true);
	}

	public <Q, R> Slice<Q> fetchSlice(Class<Q> queryClass, Class<R> rootClass,
	                                  EntityManager entityManager,
	                                  TriConsumer<CriteriaBuilder, CriteriaQuery, Root<R>> queryConfigurator,
	                                  Pageable pageable, boolean clearContext) {
		return fetchSlice(queryClass, rootClass, entityManager, queryConfigurator, null,
		                  pageable, clearContext);
	}

	public <Q, R> Slice<Q> fetchSlice(Class<Q> queryClass, Class<R> rootClass,
	                                  EntityManager entityManager,
	                                  TriConsumer<CriteriaBuilder, CriteriaQuery, Root<R>> queryConfigurator,
	                                  Pageable pageable) {
		return fetchSlice(queryClass, rootClass, entityManager, queryConfigurator, pageable, true);
	}


	public <Q> Slice<Q> fetchSlice(Class<Q> rootClass,
	                               EntityManager entityManager,
	                               TriConsumer<CriteriaBuilder, CriteriaQuery, Root<Q>> queryConfigurator,
	                               Function<EntityManager, EntityGraph<Q>> entityGraphProducer,
	                               Pageable pageable) {
		return fetchSlice(rootClass, rootClass, entityManager, queryConfigurator, entityGraphProducer, pageable);
	}

	public <Q> Slice<Q> fetchSlice(Class<Q> queryClass,
	                               EntityManager entityManager,
	                               TriConsumer<CriteriaBuilder, CriteriaQuery, Root<Q>> queryConfigurator,
	                               Pageable pageable) {
		return fetchSlice(queryClass, queryClass, entityManager, queryConfigurator, pageable, true);
	}
}
