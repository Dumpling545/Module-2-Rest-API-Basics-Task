package com.epam.esm.db.impl;

import com.epam.esm.db.GiftCertificateRepository;
import com.epam.esm.db.helper.FetchQueryHelper;
import com.epam.esm.db.helper.OrderHelper;
import com.epam.esm.model.entity.GiftCertificate;
import com.epam.esm.model.entity.GiftCertificateSearchFilter;
import com.epam.esm.model.entity.GiftCertificate_;
import com.epam.esm.model.entity.PagedResult;
import com.epam.esm.model.entity.Tag;
import com.epam.esm.model.entity.Tag_;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.EMPTY_LIST;

@Repository
@RequiredArgsConstructor
public class JpaGiftCertificateRepository implements GiftCertificateRepository {
	private static final String LOAD_GRAPH_HINT_KEY = "javax.persistence.loadgraph";
	private static final String FULL_CERTIFICATE_ENTITY_GRAPH_NAME = "full-certificate-entity-graph";
	private final FetchQueryHelper fetchQueryHelper;
	private final OrderHelper<GiftCertificate> giftCertificateOrderHelper;
	@PersistenceContext
	private EntityManager entityManager;

	@Transactional
	public GiftCertificate createCertificate(GiftCertificate certificate) {
		LocalDateTime localDateTime = LocalDateTime.now(ZoneOffset.UTC);
		certificate.setId(null);
		certificate.setCreateDate(localDateTime);
		certificate.setLastUpdateDate(localDateTime);
		GiftCertificate created = entityManager.merge(certificate);
		entityManager.flush();
		entityManager.clear();
		return created;
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<GiftCertificate> getCertificateById(int id) {
		HashMap<String, Object> params = new HashMap<>();
		params.put(LOAD_GRAPH_HINT_KEY, entityManager.getEntityGraph(FULL_CERTIFICATE_ENTITY_GRAPH_NAME));
		GiftCertificate certificate = entityManager.find(GiftCertificate.class, id, params);
		entityManager.clear();
		return Optional.ofNullable(certificate);
	}

	@Override
	@Transactional(readOnly = true)
	public PagedResult<GiftCertificate> getCertificatesByFilter(GiftCertificateSearchFilter giftCertificateSearchFilter,
	                                                            int offset, int limit) {
		PagedResult<Integer> filteredIdsResult = fetchQueryHelper.fetchPagedResult(Integer.class, GiftCertificate.class,
		                                                                           entityManager,
		                                                                           (cb, cq, r) -> configureQueryByFilter(
				                                                                           giftCertificateSearchFilter,
				                                                                           cb, cq, r),
		                                                                           offset, limit);
		PagedResult.PagedResultBuilder<GiftCertificate> builder = PagedResult.builder();
		builder.first(filteredIdsResult.isFirst()).last(filteredIdsResult.isLast());
		if (filteredIdsResult.getPage().isEmpty()) {
			return builder.page(EMPTY_LIST).build();
		}
		List<GiftCertificate> certificates = fetchQueryHelper.fetch(GiftCertificate.class, entityManager,
		                                                            (cb, cq, r) -> {
			                                                            cq.select(r).where(r.get(GiftCertificate_.id)
					                                                                               .in(filteredIdsResult.getPage()));
			                                                            if (giftCertificateSearchFilter.getSortBy() !=
			                                                                null) {
				                                                            cq.orderBy(
						                                                            giftCertificateOrderHelper.createOrder(
								                                                            giftCertificateSearchFilter.getSortBy(),
								                                                            cb, r));
			                                                            }
		                                                            },
		                                                            (em) -> (EntityGraph<GiftCertificate>) em.getEntityGraph(
				                                                            FULL_CERTIFICATE_ENTITY_GRAPH_NAME),
		                                                            TypedQuery::getResultList);
		return builder.page(certificates).build();
	}

	private void configureQueryByFilter(GiftCertificateSearchFilter giftCertificateSearchFilter,
	                                    CriteriaBuilder criteriaBuilder,
	                                    CriteriaQuery criteriaQuery,
	                                    Root<GiftCertificate> root) {
		List<Predicate> restrictions = new ArrayList<>();
		if (giftCertificateSearchFilter.getTagNames() != null && !giftCertificateSearchFilter.getTagNames().isEmpty()) {
			Join<GiftCertificate, Tag> tagJoin = root.join(GiftCertificate_.tags);
			criteriaQuery.groupBy(root.get(GiftCertificate_.id));
			criteriaQuery.having(criteriaBuilder
					                     .equal(criteriaBuilder.count(tagJoin.get(Tag_.id)),
					                            giftCertificateSearchFilter.getTagNames().size()));
			restrictions.add(tagJoin.get(Tag_.name).in(giftCertificateSearchFilter.getTagNames()));
		}
		if (giftCertificateSearchFilter.getNamePart() != null && !giftCertificateSearchFilter.getNamePart().isBlank()) {
			Expression<Integer> locateNamePart = criteriaBuilder
					.locate(root.get(GiftCertificate_.name), giftCertificateSearchFilter.getNamePart());
			restrictions.add(criteriaBuilder.greaterThan(locateNamePart, 0));
		}
		if (giftCertificateSearchFilter.getDescriptionPart() != null &&
		    !giftCertificateSearchFilter.getDescriptionPart().isBlank()) {
			Expression<Integer> locateDescPart = criteriaBuilder
					.locate(root.get(GiftCertificate_.description), giftCertificateSearchFilter.getDescriptionPart());
			restrictions.add(criteriaBuilder.greaterThan(locateDescPart, 0));
		}
		criteriaQuery.select(root.get(GiftCertificate_.id)).where(restrictions.toArray(new Predicate[0]));
		if (giftCertificateSearchFilter.getSortBy() != null) {
			Order order =
					giftCertificateOrderHelper.createOrder(giftCertificateSearchFilter.getSortBy(), criteriaBuilder,
					                                       root);
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
