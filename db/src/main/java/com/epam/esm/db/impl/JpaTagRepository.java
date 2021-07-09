package com.epam.esm.db.impl;

import com.epam.esm.db.TagRepository;
import com.epam.esm.db.helper.DatabaseHelper;
import com.epam.esm.db.helper.TriConsumer;
import com.epam.esm.model.entity.PagedResult;
import com.epam.esm.model.entity.Tag;
import com.epam.esm.model.entity.Tag_;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

@Repository
@RequiredArgsConstructor
public class JpaTagRepository implements TagRepository {
	private static final String GET_MOST_WIDELY_USED_TAG_WITH_HIGHEST_COST =
			"SELECT tag.* FROM tag " +
					"INNER JOIN tag_gift_certificate ON tag_gift_certificate.tag_id=tag.id " +
					"INNER JOIN cert_order ON cert_order.gift_certificate_id=tag_gift_certificate.gift_certificate_id " +
					"WHERE user_id=:userId GROUP BY tag.id, tag.name " +
					"ORDER BY COUNT(cert_order.id) DESC, SUM(cert_order.cost) DESC LIMIT 1";
	private static final String USER_ID_PARAM_KEY = "userId";
	@PersistenceContext
	private EntityManager entityManager;

	private final DatabaseHelper databaseHelper;

	@Transactional
	public Tag createTag(Tag tag) {
		entityManager.persist(tag);
		entityManager.flush();
		entityManager.clear();
		return tag;
	}

	@Override
	public Optional<Tag> getTagById(int id) {
		Tag tag = entityManager.find(Tag.class, id);
		entityManager.clear();
		return Optional.ofNullable(tag);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Tag> getTagByName(String tagName) {
		TriConsumer<CriteriaBuilder, CriteriaQuery, Root<Tag>> queryConfigurator = (cb, cq, r) -> {
			cq.select(r).where(cb.equal(r.get(Tag_.name), tagName));
		};
		Function<TypedQuery<Tag>, Optional<Tag>> resultProducer = tq -> tq.getResultStream().findFirst();
		return databaseHelper.fetch(Tag.class, entityManager, queryConfigurator, resultProducer);
	}

	@Override
	@Transactional(readOnly = true)
	public PagedResult<Tag> getAllTags(int offset, int limit) {
		TriConsumer<CriteriaBuilder, CriteriaQuery, Root<Tag>> queryConfigurator = (cb, cq, r) -> {
			cq.select(r);
		};
		return databaseHelper.fetchPagedResult(Tag.class, entityManager, queryConfigurator, offset, limit);
	}


	@Override
	public List<Tag> getTagsFromNameSet(Set<String> tagNames) {
		TriConsumer<CriteriaBuilder, CriteriaQuery, Root<Tag>> queryConfigurator = (cb, cq, r) -> {
			cq.select(r).where(r.get(Tag_.name).in(tagNames));
		};
		Function<TypedQuery<Tag>, List<Tag>> resultProducer = TypedQuery::getResultList;
		return databaseHelper.fetch(Tag.class, entityManager, queryConfigurator, resultProducer);
	}

	@Transactional
	public boolean deleteTag(int id) {
		Tag tag = entityManager.find(Tag.class, id);
		boolean found = tag != null;
		if (found) {
			entityManager.remove(tag);
		}
		entityManager.flush();
		entityManager.clear();
		return found;
	}

	@Override
	public Optional<Tag> getMostWidelyUsedTagOfUserWithHighestCostOfAllOrders(int userId) {
		Query query = entityManager.createNativeQuery(GET_MOST_WIDELY_USED_TAG_WITH_HIGHEST_COST, Tag.class);
		query.setParameter(USER_ID_PARAM_KEY, userId);
		Optional<Tag> result = ((List<Tag>) query.getResultList()).stream().findFirst();
		entityManager.clear();
		return result;
	}
}
