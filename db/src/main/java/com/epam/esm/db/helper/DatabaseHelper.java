package com.epam.esm.db.helper;

import org.springframework.stereotype.Component;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.function.Function;

@Component
public class DatabaseHelper {

	public <Q, O> O execute(Class<Q> queryClass, EntityManager entityManager,
	                        TriConsumer<CriteriaBuilder, CriteriaQuery<Q>, Root<Q>> queryConfigurator,
	                        Function<TypedQuery<Q>, O> resultProducer) {
		return execute(queryClass, entityManager, queryConfigurator, null, resultProducer);
	}

	public <Q, O> O execute(Class<Q> queryClass, EntityManager entityManager,
	                        TriConsumer<CriteriaBuilder, CriteriaQuery<Q>, Root<Q>> queryConfigurator,
	                        Function<EntityManager, EntityGraph<Q>> entityGraphProducer,
	                        Function<TypedQuery<Q>, O> resultProducer) {
		return execute(queryClass, queryClass, entityManager, queryConfigurator, entityGraphProducer, resultProducer,
				true);
	}

	public <Q, R, O> O execute(Class<Q> queryClass, Class<R> rootClass, EntityManager entityManager,
	                           TriConsumer<CriteriaBuilder, CriteriaQuery<Q>, Root<R>> queryConfigurator,
	                           Function<TypedQuery<Q>, O> outputProducer, boolean clearContext) {
		return execute(queryClass, rootClass, entityManager, queryConfigurator, null, outputProducer, clearContext);
	}

	public <Q, R, O> O execute(Class<Q> queryClass, Class<R> rootClass, EntityManager entityManager,
	                           TriConsumer<CriteriaBuilder, CriteriaQuery<Q>, Root<R>> queryConfigurator,
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
}
