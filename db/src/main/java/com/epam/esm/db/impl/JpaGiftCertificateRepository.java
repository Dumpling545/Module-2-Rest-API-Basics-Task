package com.epam.esm.db.impl;

import com.epam.esm.db.GiftCertificateRepository;
import com.epam.esm.db.helper.DatabaseHelper;
import com.epam.esm.model.entity.Filter;
import com.epam.esm.model.entity.GiftCertificate;
import com.epam.esm.model.entity.GiftCertificate_;
import com.epam.esm.model.entity.PagedResult;
import com.epam.esm.model.entity.SortOption;
import com.epam.esm.model.entity.Tag;
import com.epam.esm.model.entity.Tag_;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.GenerationType;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
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
@RequiredArgsConstructor
public class JpaGiftCertificateRepository implements GiftCertificateRepository {
	@PersistenceContext
	private EntityManager entityManager;

	private final DatabaseHelper databaseHelper;
	private static final String LOAD_GRAPH_HINT_KEY = "javax.persistence.loadgraph";
	private static final String FULL_CERTIFICATE_ENTITY_GRAPH_NAME = "full-certificate-entity-graph";

	@Transactional
	public GiftCertificate createCertificate(GiftCertificate certificate) {
		LocalDateTime localDateTime = LocalDateTime.now(ZoneOffset.UTC);
		certificate.setCreateDate(localDateTime);
		certificate.setLastUpdateDate(localDateTime);
		entityManager.persist(certificate);
		entityManager.flush();
		entityManager.clear();
		return certificate;
	}

	@Override
	public Optional<GiftCertificate> getCertificateById(int id) {
		HashMap<String, Object> params = new HashMap<>();
		params.put(LOAD_GRAPH_HINT_KEY, entityManager.getEntityGraph(FULL_CERTIFICATE_ENTITY_GRAPH_NAME));
		GiftCertificate certificate = entityManager.find(GiftCertificate.class, id, params);
		entityManager.clear();
		return Optional.ofNullable(certificate);
	}

	@Override
	public PagedResult<GiftCertificate> getCertificatesByFilter(Filter filter, int offset, int limit) {
		PagedResult<Integer> filteredIdsResult = databaseHelper.fetchPagedResult(Integer.class, GiftCertificate.class,
				entityManager,
				(cb, cq, r) -> configureQueryByFilter(filter, cb, cq, r),
				offset, limit);
		List<GiftCertificate> certificates = databaseHelper.fetch(GiftCertificate.class, entityManager,
				(cb, cq, r) -> {
					cq.select(r).where(r.get(GiftCertificate_.id).in(filteredIdsResult.getPage()))
							.orderBy(createOrderFromSortOption(filter.getSortBy(), cb, r));
				},
				(em) -> (EntityGraph<GiftCertificate>) em.getEntityGraph(FULL_CERTIFICATE_ENTITY_GRAPH_NAME),
				TypedQuery::getResultList);
		PagedResult<GiftCertificate> pagedResult = PagedResult.<GiftCertificate>builder()
				.first(filteredIdsResult.isFirst())
				.last(filteredIdsResult.isLast())
				.page(certificates)
				.build();
		return pagedResult;
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
	                                    CriteriaQuery criteriaQuery,
	                                    Root<GiftCertificate> root) {
		List<Predicate> restrictions = new ArrayList<>();
		if (filter.getTagNames() != null) {
			Join<GiftCertificate, Tag> tagJoin = root.join(GiftCertificate_.tags);
			criteriaQuery.groupBy(root.get(GiftCertificate_.id));
			criteriaQuery.having(criteriaBuilder
					.equal(criteriaBuilder.count(tagJoin.get(Tag_.id)), filter.getTagNames().size()));
			restrictions.add(tagJoin.get(Tag_.name).in(filter.getTagNames()));
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
		criteriaQuery.select(root.get(GiftCertificate_.id)).where(restrictions.toArray(new Predicate[0]));
		if (filter.getSortBy() != null) {
			Order order = createOrderFromSortOption(filter.getSortBy(), criteriaBuilder, root);
			criteriaQuery.orderBy(order);
		}
	}

	@Transactional
	public void updateCertificate(GiftCertificate certificate) {
		certificate.setLastUpdateDate(LocalDateTime.now(ZoneOffset.UTC));
		entityManager.merge(certificate);
		entityManager.flush();
		entityManager.clear();
	}

	@Transactional
	public boolean deleteCertificate(int id) {
		GiftCertificate giftCertificate = entityManager.find(GiftCertificate.class, id);
		boolean found = giftCertificate != null;
		if (found) {
			entityManager.remove(giftCertificate);
		}
		entityManager.flush();
		entityManager.clear();
		return found;
	}
}
