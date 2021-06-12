package com.epam.esm.db.impl;

import com.epam.esm.db.GiftCertificateRepository;
import com.epam.esm.model.entity.Filter;
import com.epam.esm.model.entity.GiftCertificate;
import com.epam.esm.model.entity.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.function.LongFunction;
import java.util.logging.Level;
import java.util.logging.Logger;

@Repository
public class JdbcGiftCertificateRepository implements GiftCertificateRepository {
	private JdbcOperations jdbcOperations;
	private SimpleJdbcInsert simpleJdbcInsert;
	private static final String CERTIFICATE_TABLE = "gift_certificate";
	//SQL Table Column Labels
	private static final String ID = "id";
	private static final String NAME = "name";
	private static final String DESCRIPTION = "description";
	private static final String DURATION = "duration";
	private static final String PRICE = "price";
	private static final String CREATE_DATE = "create_date";
	private static final String LAST_UPDATE_DATE = "last_update_date";
	//SQL Template Queries
	private static final String GET_ALL_CERTIFICATES_SQL =
			"SELECT id, name, description, duration, price, " + "create_date, last_update_date FROM gift_certificate";
	private static final String GET_CERTIFICATE_BY_ID_SQL = GET_ALL_CERTIFICATES_SQL + " WHERE id=?";
	private static final String UPDATE_CERTIFICATE_SQL =
			"UPDATE gift_certificate SET name = ?, " + "description = ?, duration = ?, price = ?, " +
					"last_update_date = ? WHERE id=?";
	private static final String DELETE_CERTIFICATE_SQL = "DELETE FROM gift_certificate WHERE id=?";
	private static final String ADD_TAG_TO_CERTIFICATE_SQL =
			"INSERT INTO " + "tag_gift_certificate(gift_certificate_id, tag_id) VALUES (?,?)";
	private static final String DELETE_TAG_FROM_CERTIFICATE_SQL =
			"DELETE FROM tag_gift_certificate " + "WHERE gift_certificate_id=? AND tag_id=?";
	//Incomplete SQL Templates
	private static final String FILTER_BY_TAG_ID_SQL_INC =
			" INNER JOIN " + "tag_gift_certificate ON gift_certificate.id=" +
					"tag_gift_certificate.gift_certificate_id " + "WHERE tag_id=? ";
	private static final String FILTER_BY_NAME_PART_SQL_INC = " name LIKE ? ";
	private static final String FILTER_BY_DESCRIPTION_PART_SQL_INC = " description LIKE ? ";
	private static final String AND_SQL_INC = " AND ";
	private static final String WHERE_SQL_INC = " WHERE ";

	@Autowired
	public JdbcGiftCertificateRepository(JdbcOperations jdbcOperations, DataSource dataSource) {
		this.jdbcOperations = jdbcOperations;
		simpleJdbcInsert =
				new SimpleJdbcInsert(dataSource).withTableName(CERTIFICATE_TABLE).usingGeneratedKeyColumns(ID);
	}

	private GiftCertificate mapCertificate(ResultSet rs, int row) throws SQLException {
		return new GiftCertificate(rs.getInt(ID), rs.getString(NAME), rs.getString(DESCRIPTION), rs.getDouble(PRICE),
				rs.getInt(DURATION), rs.getTimestamp(CREATE_DATE).toLocalDateTime(),
				rs.getTimestamp(LAST_UPDATE_DATE).toLocalDateTime());
	}

	@Override
	public void createCertificate(GiftCertificate certificate) {
		LocalDateTime localDateTime = LocalDateTime.now(ZoneOffset.UTC);
		Timestamp timestamp = Timestamp.valueOf(localDateTime);
		Map<String, Object> parameters = new HashMap<>(6);
		parameters.put(NAME, certificate.getName());
		parameters.put(DESCRIPTION, certificate.getDescription());
		parameters.put(PRICE, certificate.getPrice());
		parameters.put(DURATION, certificate.getDuration());
		parameters.put(CREATE_DATE, timestamp);
		parameters.put(LAST_UPDATE_DATE, timestamp);
		certificate.setId(simpleJdbcInsert.executeAndReturnKey(parameters).intValue());
		certificate.setCreateDate(localDateTime);
		certificate.setLastUpdateDate(localDateTime);
	}

	@Override
	public Optional<GiftCertificate> getCertificateById(int id) {
		return Optional.ofNullable(DataAccessUtils
				.singleResult(jdbcOperations.query(GET_CERTIFICATE_BY_ID_SQL, this::mapCertificate, id)));
	}

	@Override
	public List<GiftCertificate> getCertificatesByFilter(Filter filter) {
		List<Object> parameters = new ArrayList<>();
		String sql = buildSqlFromFilter(filter, parameters);
		return jdbcOperations.query(sql, this::mapCertificate, parameters.toArray());
	}

	private String buildSqlFromFilter(Filter filter, List<Object> parameters) {
		StringBuilder sb = new StringBuilder(GET_ALL_CERTIFICATES_SQL);
		String joiner = WHERE_SQL_INC;
		if (filter.getTagId() != Filter.NO_TAG_SPECIFIED) {
			sb.append(FILTER_BY_TAG_ID_SQL_INC);
			parameters.add(filter.getTagId());
			joiner = AND_SQL_INC;
		}
		String[] substrs = {filter.getNamePart(), filter.getDescriptionPart()};
		String[] queries = {FILTER_BY_NAME_PART_SQL_INC, FILTER_BY_DESCRIPTION_PART_SQL_INC};
		for (int i = 0; i < 2; i++) {
			if (substrs[i] != null && !substrs[i].isBlank()) {
				sb.append(joiner).append(queries[i]);
				parameters.add("%" + substrs[i] + "%");
				joiner = AND_SQL_INC;
			}
		}
		if (filter.getSortBy() != null) {
			sb.append(filter.getSortBy().getSql());
		}
		return sb.toString();
	}


	@Override
	public void updateCertificate(GiftCertificate certificate) {
		LocalDateTime localDateTime = LocalDateTime.now(ZoneOffset.UTC);
		jdbcOperations.update(UPDATE_CERTIFICATE_SQL, certificate.getName(), certificate.getDescription(),
				certificate.getDuration(), certificate.getPrice(), localDateTime, certificate.getId());
		certificate.setLastUpdateDate(localDateTime);
	}

	@Override
	public boolean deleteCertificate(int id) {
		return jdbcOperations.update(DELETE_CERTIFICATE_SQL, id) > 0;
	}

	@Override
	public void addTag(int certificateId, int tagId) {
		jdbcOperations.update(ADD_TAG_TO_CERTIFICATE_SQL, certificateId, tagId);
	}

	@Override
	public void removeTag(int certificateId, int tagId) {
		jdbcOperations.update(DELETE_TAG_FROM_CERTIFICATE_SQL, certificateId, tagId);
	}
}
