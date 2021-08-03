package com.epam.esm.db.impl;

import com.epam.esm.db.fragment.FilteredGiftCertificateRepository;
import com.epam.esm.db.helper.FetchQueryHelper;
import com.epam.esm.model.entity.GiftCertificate;
import com.epam.esm.model.entity.GiftCertificateSearchFilter;
import com.epam.esm.model.entity.GiftCertificate_;
import com.epam.esm.model.entity.Tag;
import com.epam.esm.model.entity.Tag_;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.jpa.repository.query.QueryUtils;
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
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class GiftCertificateRepositoryImpl implements FilteredGiftCertificateRepository {
	private static final String FULL_CERTIFICATE_ENTITY_GRAPH_NAME = "full-certificate-entity-graph";
	private final FetchQueryHelper fetchQueryHelper;
	@PersistenceContext
	private EntityManager entityManager;

	@Override
	@Transactional(readOnly = true)
	public Slice<GiftCertificate> getCertificatesByFilter(GiftCertificateSearchFilter giftCertificateSearchFilter,
	                                                      Pageable pageable) {
		Slice<Integer> sliceWithIds = fetchQueryHelper.fetchSlice(Integer.class, GiftCertificate.class,
		                                                          entityManager,
		                                                          (cb, cq, r) -> configureQueryByFilter(
				                                                          giftCertificateSearchFilter,
				                                                          pageable, cb, cq, r),
		                                                          pageable);
		if (sliceWithIds.isEmpty()) {
			return sliceWithIds.map(i -> GiftCertificate.builder().build());
		}
		List<GiftCertificate> certificates = fetchQueryHelper.fetch(GiftCertificate.class, entityManager,
		                                                            (cb, cq, r) -> {
			                                                            cq.select(r).where(r.get(GiftCertificate_.id)
					                                                                               .in(sliceWithIds.getContent()));
			                                                            cq.orderBy(
					                                                            QueryUtils.toOrders(pageable.getSort(),
					                                                                                r, cb));
		                                                            },
		                                                            (em) -> (EntityGraph<GiftCertificate>) em.getEntityGraph(
				                                                            FULL_CERTIFICATE_ENTITY_GRAPH_NAME),
		                                                            TypedQuery::getResultList);
		return new SliceImpl(certificates, pageable, sliceWithIds.hasNext());
	}

	private void configureQueryByFilter(GiftCertificateSearchFilter giftCertificateSearchFilter, Pageable pageable,
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
		List<Order> sortOrders = QueryUtils.toOrders(pageable.getSort(), root, criteriaBuilder);
		criteriaQuery.orderBy(sortOrders);
	}
}
