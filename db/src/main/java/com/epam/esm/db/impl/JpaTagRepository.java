package com.epam.esm.db.impl;

import com.epam.esm.db.TagRepository;
import com.epam.esm.db.helper.DatabaseHelper;
import com.epam.esm.db.helper.TriConsumer;
import com.epam.esm.model.entity.Tag;
import com.epam.esm.model.entity.Tag_;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

@Repository
public class JpaTagRepository implements TagRepository {
	@PersistenceContext
	private EntityManager entityManager;

	private DatabaseHelper databaseHelper;

	public JpaTagRepository(DatabaseHelper databaseHelper) {
		this.databaseHelper = databaseHelper;
	}

	@Transactional
	public Tag createTag(Tag tag) {
		entityManager.persist(tag);
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
	public Optional<Tag> getTagByName(String tagName) {
		TriConsumer<CriteriaBuilder, CriteriaQuery<Tag>, Root<Tag>> queryConfigurator = (cb, cq, r) -> {
			cq.select(r).where(cb.equal(r.get(Tag_.name), tagName));
		};
		Function<TypedQuery<Tag>, Optional<Tag>> resultProducer = tq -> tq.getResultStream().findFirst();
		return databaseHelper.execute(Tag.class, entityManager, queryConfigurator, resultProducer);
	}

	@Override
	public List<Tag> getAllTags() {
		TriConsumer<CriteriaBuilder, CriteriaQuery<Tag>, Root<Tag>> queryConfigurator = (cb, cq, r) -> {
			cq.select(r);
		};
		Function<TypedQuery<Tag>, List<Tag>> resultProducer = TypedQuery::getResultList;
		return databaseHelper.execute(Tag.class, entityManager, queryConfigurator, resultProducer);
	}
	@Override
	public List<Tag> getTagsFromNameSet(Set<String> tagNames) {
		TriConsumer<CriteriaBuilder, CriteriaQuery<Tag>, Root<Tag>> queryConfigurator = (cb, cq, r) -> {
			cq.select(r).where(r.get(Tag_.name).in(tagNames));
		};
		Function<TypedQuery<Tag>, List<Tag>> resultProducer = TypedQuery::getResultList;
		return databaseHelper.execute(Tag.class, entityManager, queryConfigurator, resultProducer);
	}

	@Transactional
	public boolean deleteTag(int id) {
		Tag tag = entityManager.find(Tag.class, id);
		boolean found = tag != null;
		if (found) {
			entityManager.remove(tag);
		}
		return found;
	}
}
