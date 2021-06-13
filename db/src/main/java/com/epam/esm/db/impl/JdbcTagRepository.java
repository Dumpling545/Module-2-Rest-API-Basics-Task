package com.epam.esm.db.impl;

import com.epam.esm.db.TagRepository;
import com.epam.esm.model.entity.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class JdbcTagRepository implements TagRepository {
	@Value("${tag.sql.table.name}")
	private String tagTable;
	//SQL Table Column and Parameter Labels
	@Value("${tag.sql.column.id}")
	private String idColumn;
	@Value("${tag.sql.column.name}")
	private String nameColumn;
	@Value("${tag.sql.column.gift-certificate-id}")
	private String giftCertificateIdColumn;
	@Value("${tag.sql.param-key.names}")
	private String namesParamKey;
	//SQL Template Queries
	@Value("${tag.sql.query.get-all}")
	private String getAllTagsSql;
	@Value("${tag.sql.query.get-by-id}")
	private String getTagByIdSql;
	@Value("${tag.sql.query.get-by-name}")
	private String getTagByNameSql;
	@Value("${tag.sql.query.get-by-certificate}")
	private String getTagsByCertificateSql;
	@Value("${tag.sql.query.get-from-name-se}")
	private String getTagsFromNameSet;
	@Value("${tag.sql.query.delete-by-id}")
	private String deleteTagSql;

	private NamedParameterJdbcOperations jdbcOperations;
	private SimpleJdbcInsert simpleJdbcInsert;

	@Autowired
	public JdbcTagRepository(NamedParameterJdbcOperations jdbcOperations, DataSource dataSource) {
		this.jdbcOperations = jdbcOperations;
		simpleJdbcInsert = new SimpleJdbcInsert(dataSource).withTableName(tagTable).usingGeneratedKeyColumns(idColumn);
	}

	private Tag mapTag(ResultSet rs, int row) throws SQLException {
		return new Tag(rs.getInt(idColumn), rs.getString(nameColumn));
	}

	@Override
	public Tag createTag(Tag tag) {
		Map<String, Object> parameters = new HashMap<>(1);
		parameters.put(nameColumn, tag.getName());
		int id = simpleJdbcInsert.executeAndReturnKey(parameters).intValue();
		return new Tag(id, tag.getName());
	}

	@Override
	public Optional<Tag> getTagById(int id) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(idColumn, id);
		return Optional.ofNullable(
				DataAccessUtils.singleResult(jdbcOperations.query(getTagByIdSql, parameters, this::mapTag)));
	}

	@Override
	public Optional<Tag> getTagByName(String tagName) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(nameColumn, tagName);
		return Optional.ofNullable(
				DataAccessUtils.singleResult(jdbcOperations.query(getTagByNameSql, parameters, this::mapTag)));
	}

	@Override
	public List<Tag> getAllTags() {
		return jdbcOperations.query(getAllTagsSql, this::mapTag);
	}

	@Override
	public List<Tag> getTagsByCertificate(int certificateId) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(giftCertificateIdColumn, certificateId);
		return jdbcOperations.query(getTagsByCertificateSql, parameters, this::mapTag);
	}

	@Override
	public List<Tag> getTagsFromNameSet(Set<String> tagNames) {
		SqlParameterSource parameters = new MapSqlParameterSource(namesParamKey, tagNames);
		return jdbcOperations.query(getTagsFromNameSet, parameters, this::mapTag);
	}

	@Override
	public boolean deleteTag(int id) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(idColumn, id);
		return jdbcOperations.update(deleteTagSql, parameters) > 0;
	}
}
