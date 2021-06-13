package com.epam.esm.db.impl;

import com.epam.esm.db.TagRepository;
import com.epam.esm.model.entity.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class JdbcTagRepository implements TagRepository {
	private static final String TAG_TABLE = "tag";
	//SQL Table Column Labels
	private static final String ID = "id";
	private static final String NAME = "name";
	//SQL Template Queries
	private static final String GET_ALL_TAGS_SQL = "SELECT id, name FROM tag";
	private static final String GET_TAG_BY_ID_SQL = GET_ALL_TAGS_SQL + " WHERE id=?";
	private static final String GET_TAG_BY_NAME_SQL = GET_ALL_TAGS_SQL + " WHERE name=?";
	private static final String GET_TAGS_BY_CERTIFICATE_SQL =
			GET_ALL_TAGS_SQL + " INNER JOIN " + "tag_gift_certificate ON tag.id=tag_gift_certificate.tag_id " +
					"WHERE gift_certificate_id=?";
	private static final String DELETE_TAG_SQL = "DELETE FROM tag WHERE id=?";
	private JdbcOperations jdbcOperations;
	private SimpleJdbcInsert simpleJdbcInsert;

	@Autowired
	public JdbcTagRepository(JdbcOperations jdbcOperations, DataSource dataSource) {
		this.jdbcOperations = jdbcOperations;
		simpleJdbcInsert = new SimpleJdbcInsert(dataSource).withTableName(TAG_TABLE).usingGeneratedKeyColumns(ID);
	}

	private Tag mapTag(ResultSet rs, int row) throws SQLException {
		return new Tag(rs.getInt(ID), rs.getString(NAME));
	}

	@Override
	public void createTag(Tag tag) {
		Map<String, Object> parameters = new HashMap<>(1);
		parameters.put(NAME, tag.getName());
		tag.setId(simpleJdbcInsert.executeAndReturnKey(parameters).intValue());
	}

	@Override
	public Optional<Tag> getTagById(int id) {
		return Optional
				.ofNullable(DataAccessUtils.singleResult(jdbcOperations.query(GET_TAG_BY_ID_SQL, this::mapTag, id)));
	}

	@Override
	public Optional<Tag> getTagByName(String tagName) {
		return Optional.ofNullable(
				DataAccessUtils.singleResult(jdbcOperations.query(GET_TAG_BY_NAME_SQL, this::mapTag, tagName)));
	}

	@Override
	public List<Tag> getAllTags() {
		return jdbcOperations.query(GET_ALL_TAGS_SQL, this::mapTag);
	}

	@Override
	public List<Tag> getTagsByCertificate(int certificateId) {
		return jdbcOperations.query(GET_TAGS_BY_CERTIFICATE_SQL, this::mapTag, certificateId);
	}

	@Override
	public boolean deleteTag(int id) {

		return jdbcOperations.update(DELETE_TAG_SQL, id) > 0;
	}
}
