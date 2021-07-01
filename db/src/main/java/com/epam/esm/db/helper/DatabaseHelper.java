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
	public <E, R> R execute(Class<E> entityClass, EntityManager entityManager,
	                        TriConsumer<CriteriaBuilder, CriteriaQuery<E>, Root<E>> queryConfigurator,
	                        Function<TypedQuery<E>, R> resultProducer) {
		return execute(entityClass, entityManager, queryConfigurator, null, resultProducer);
	}
	public <E, R> R execute(Class<E> entityClass, EntityManager entityManager,
	                        TriConsumer<CriteriaBuilder, CriteriaQuery<E>, Root<E>> queryConfigurator,
	                        Function<EntityManager, EntityGraph<E>> entityGraphProducer,
	                        Function<TypedQuery<E>, R> resultProducer) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<E> criteriaQuery = criteriaBuilder.createQuery(entityClass);
		Root<E> root = criteriaQuery.from(entityClass);
		queryConfigurator.accept(criteriaBuilder, criteriaQuery, root);
		TypedQuery<E> typedQuery = entityManager.createQuery(criteriaQuery);
		if(entityGraphProducer != null){
			typedQuery.setHint("javax.persistence.loadgraph", entityGraphProducer.apply(entityManager));
		}
		R result = resultProducer.apply(typedQuery);
		entityManager.clear();
		return result;
	}
}
