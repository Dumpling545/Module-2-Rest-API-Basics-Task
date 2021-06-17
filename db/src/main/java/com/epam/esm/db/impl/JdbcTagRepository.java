package com.epam.esm.db.impl;

import com.epam.esm.db.TagRepository;
import com.epam.esm.model.entity.Tag;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Repository
public class JdbcTagRepository implements TagRepository {
	//SQL Table Column Labels
	private final static String ID_COLUMN = "id";
	//SQL Parameter keys
	private final static String NAME_COLUMN = "name";
	private final static String GIFT_CERTIFICATE_ID_PARAM_KEY = "gift_certificate_id";
	private final static String ID_PARAM_KEY = ID_COLUMN;
	private final static String NAME_PARAM_KEY = "name";
	private final static String NAMES_PARAM_KEY = "names";
	//SQL Template Queries
	private final static String GET_ALL_TAGS_SQL = "SELECT id, name FROM tag";
	private final static String GET_TAG_BY_ID_SQL = "SELECT id, name FROM tag WHERE id=:id";
	private final static String GET_TAG_BY_NAME_SQL = "SELECT id, name FROM tag WHERE name=:name";
	private final static String GET_TAGS_BY_CERTIFICATE_SQL =
			"SELECT id, name FROM tag INNER JOIN tag_gift_certificate ON tag.id=tag_gift_certificate.tag_id " +
					"WHERE gift_certificate_id=:gift_certificate_id";
	private final static String GET_TAGS_FROM_NAME_SET = "SELECT id, name FROM tag WHERE name IN (:names)";
	private final static String DELETE_TAG_SQL = "DELETE FROM tag WHERE id=:id";
	//Table name
	private final static String TAG_TABLE_NAME = "tag";
	private NamedParameterJdbcOperations jdbcOperations;
	private SimpleJdbcInsert simpleJdbcInsert;

	public JdbcTagRepository(NamedParameterJdbcOperations jdbcOperations, DataSource dataSource) {
		this.jdbcOperations = jdbcOperations;
		simpleJdbcInsert =
				new SimpleJdbcInsert(dataSource).withTableName(TAG_TABLE_NAME).usingGeneratedKeyColumns(ID_COLUMN);
	}

	private Tag mapTag(ResultSet rs, int row) throws SQLException {
		return new Tag(rs.getInt(ID_COLUMN), rs.getString(NAME_COLUMN));
	}

	@Override
	public Tag createTag(Tag tag) {
		Map<String, Object> parameters = new HashMap<>();
		parameters.put(NAME_COLUMN, tag.getName());
		int id = simpleJdbcInsert.executeAndReturnKey(parameters).intValue();
		return new Tag(id, tag.getName());
	}

	@Override
	public Optional<Tag> getTagById(int id) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(ID_PARAM_KEY, id);
		return Optional.ofNullable(
				DataAccessUtils.singleResult(jdbcOperations.query(GET_TAG_BY_ID_SQL, parameters, this::mapTag)));
	}

	@Override
	public Optional<Tag> getTagByName(String tagName) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(NAME_PARAM_KEY, tagName);
		return Optional.ofNullable(
				DataAccessUtils.singleResult(jdbcOperations.query(GET_TAG_BY_NAME_SQL, parameters, this::mapTag)));
	}

	@Override
	public List<Tag> getAllTags() {
		return jdbcOperations.query(GET_ALL_TAGS_SQL, this::mapTag);
	}

	@Override
	public List<Tag> getTagsByCertificate(int certificateId) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(GIFT_CERTIFICATE_ID_PARAM_KEY, certificateId);
		return jdbcOperations.query(GET_TAGS_BY_CERTIFICATE_SQL, parameters, this::mapTag);
	}

	@Override
	public List<Tag> getTagsFromNameSet(Set<String> tagNames) {
		SqlParameterSource parameters = new MapSqlParameterSource(NAMES_PARAM_KEY, tagNames);
		return jdbcOperations.query(GET_TAGS_FROM_NAME_SET, parameters, this::mapTag);
	}

	@Override
	public boolean deleteTag(int id) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(ID_PARAM_KEY, id);
		return jdbcOperations.update(DELETE_TAG_SQL, parameters) > 0;
	}
}
