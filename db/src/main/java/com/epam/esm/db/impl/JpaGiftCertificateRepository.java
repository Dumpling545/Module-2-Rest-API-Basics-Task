package com.epam.esm.db.impl;

import com.epam.esm.db.GiftCertificateRepository;
import com.epam.esm.db.helper.DatabaseHelper;
import com.epam.esm.db.helper.TriConsumer;
import com.epam.esm.model.entity.Filter;
import com.epam.esm.model.entity.GiftCertificate;
import com.epam.esm.model.entity.GiftCertificate_;
import com.epam.esm.model.entity.SortOption;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Repository
public class JpaGiftCertificateRepository implements GiftCertificateRepository {
	@PersistenceContext
	private EntityManager entityManager;

	private DatabaseHelper databaseHelper;
	private static final String LOAD_GRAPH_HINT_KEY = "javax.persistence.loadgraph";

	public JpaGiftCertificateRepository(DatabaseHelper databaseHelper) {
		this.databaseHelper = databaseHelper;
	}

	@Transactional
	public GiftCertificate createCertificate(GiftCertificate certificate) {
		LocalDateTime localDateTime = LocalDateTime.now(ZoneOffset.UTC);
		certificate.setCreateDate(localDateTime);
		certificate.setLastUpdateDate(localDateTime);
		entityManager.persist(certificate);
		entityManager.clear();
		return certificate;
	}

	private EntityGraph<GiftCertificate> produceEntityGraph(EntityManager entityManager) {
		EntityGraph<GiftCertificate> entityGraph = entityManager.createEntityGraph(GiftCertificate.class);
		entityGraph.addSubgraph(GiftCertificate_.tags);
		return entityGraph;
	}

	@Override
	public Optional<GiftCertificate> getCertificateById(int id) {
		HashMap<String, Object> params = new HashMap<>();
		params.put(LOAD_GRAPH_HINT_KEY, produceEntityGraph(entityManager));
		GiftCertificate certificate = entityManager.find(GiftCertificate.class, id, params);
		return Optional.ofNullable(certificate);
	}

	private Order createOrderFromSortOption(SortOption sortOption, CriteriaBuilder criteriaBuilder,
	                                        Root<GiftCertificate> root) {
		Function<Expression<?>, Order> direction = switch (sortOption.getDirection()) {
			case ASC -> criteriaBuilder::asc;
			case DESC -> criteriaBuilder::desc;
		};
		Path<?> sortCriteria = switch (sortOption.getField()) {
			case NAME -> root.get(GiftCertificate_.name);
			case CREATE_DATE -> root.get(GiftCertificate_.createDate);
			case LAST_UPDATE_DATE -> root.get(GiftCertificate_.lastUpdateDate);
		};
		return direction.apply(sortCriteria);
	}

	private void configureQueryByFilter(Filter filter, CriteriaBuilder criteriaBuilder,
	                                    CriteriaQuery<GiftCertificate> criteriaQuery,
	                                    Root<GiftCertificate> root) {
		List<Predicate> restrictions = new ArrayList<>();
		if (filter.getTag() != null) {
			restrictions.add(criteriaBuilder.isMember(filter.getTag(), root.get(GiftCertificate_.tags)));
		}
		if (filter.getNamePart() != null && !filter.getNamePart().isBlank()) {
			Expression<Integer> locateNamePart = criteriaBuilder
					.locate(root.get(GiftCertificate_.name), filter.getNamePart());
			restrictions.add(criteriaBuilder.greaterThan(locateNamePart, 0));
		}
		if (filter.getDescriptionPart() != null && !filter.getDescriptionPart().isBlank()) {
			Expression<Integer> locateDescPart = criteriaBuilder
					.locate(root.get(GiftCertificate_.description), filter.getDescriptionPart());
			restrictions.add(criteriaBuilder.greaterThan(locateDescPart, 0));
		}
		criteriaQuery.select(root).where(restrictions.toArray(new Predicate[0]));
		if (filter.getSortBy() != null) {
			Order order = createOrderFromSortOption(filter.getSortBy(), criteriaBuilder, root);
			criteriaQuery.orderBy(order);
		}
	}

	@Override
	public List<GiftCertificate> getCertificatesByFilter(Filter filter) {
		TriConsumer<CriteriaBuilder, CriteriaQuery<GiftCertificate>, Root<GiftCertificate>> queryConfigurator =
				(cb, cq, r) -> configureQueryByFilter(filter, cb, cq, r);
		List<GiftCertificate> result = databaseHelper.execute(GiftCertificate.class, entityManager, queryConfigurator,
				this::produceEntityGraph, TypedQuery::getResultList);
		return result;
	}

	@Transactional
	public void updateCertificate(GiftCertificate certificate) {
		certificate.setLastUpdateDate(LocalDateTime.now(ZoneOffset.UTC));
		entityManager.merge(certificate);
	}

	@Transactional
	public boolean deleteCertificate(int id) {
		GiftCertificate giftCertificate = entityManager.find(GiftCertificate.class, id);
		boolean found = giftCertificate != null;
		if (found) {
			entityManager.remove(giftCertificate);
		}
		return found;
	}
}
