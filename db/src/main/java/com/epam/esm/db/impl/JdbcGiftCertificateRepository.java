package com.epam.esm.db.impl;

import com.epam.esm.db.GiftCertificateRepository;
import com.epam.esm.model.entity.Filter;
import com.epam.esm.model.entity.GiftCertificate;
import com.epam.esm.model.entity.SortOption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class JdbcGiftCertificateRepository implements GiftCertificateRepository {
	//SQL Table Column Labels
	private final static String ID_COLUMN = "id";
	private final static String NAME_COLUMN = "name";
	private final static String DESCRIPTION_COLUMN = "description";
	private final static String DURATION_COLUMN = "duration";
	private final static String PRICE_COLUMN = "price";
	private final static String CREATE_DATE_COLUMN = "create_date";
	private final static String LAST_UPDATE_DATE_COLUMN = "last_update_date";
	//SQL Parameter Keys
	private final static String ID_PARAM_KEY = "id";
	private final static String GIFT_CERTIFICATE_ID_PARAM_KEY = "gift_certificate_id";
	private final static String NAME_PARAM_KEY = "name";
	private final static String NAME_PATTERN_PARAM_KEY = "name_pattern";
	private final static String DESCRIPTION_PARAM_KEY = "description";
	private final static String DESCRIPTION_PATTERN_PARAM_KEY = "description_pattern";
	private final static String DURATION_PARAM_KEY = "duration";
	private final static String PRICE_PARAM_KEY = "price";
	private final static String LAST_UPDATE_DATE_PARAM_KEY = "last_update_date";
	private final static String TAG_ID_PARAM_KEY = "tag_id";
	private final static String TAG_NAME_PARAM_KEY = "tag_name";
	//SQL Template Queries
	@Value("${}")
	private final static String GET_ALL_CERTIFICATES_SQL =
			"SELECT gc.id, gc.name, gc.description, gc.duration, gc.price, gc.create_date, gc.last_update_date " +
					"FROM gift_certificate gc";
	private final static String GET_CERTIFICATE_BY_ID_SQL =
			"SELECT gc.id, gc.name, gc.description, gc.duration, gc.price, gc.create_date, gc.last_update_date " +
					"FROM gift_certificate gc WHERE id=:id";
	private final static String UPDATE_CERTIFICATE_SQL =
			"UPDATE gift_certificate SET name=:name, description=:description, duration=:duration, price=:price, last_update_date=:last_update_date " +
					"WHERE id=:id";
	private final static String DELETE_CERTIFICATE_SQL = "DELETE FROM gift_certificate WHERE id=:id";
	private final static String ADD_TAG_TO_CERTIFICATE_SQL =
			"INSERT INTO tag_gift_certificate(gift_certificate_id, tag_id) VALUES (:gift_certificate_id, :tag_id)";
	private final static String DELETE_TAG_FROM_CERTIFICATE_SQL =
			"DELETE FROM tag_gift_certificate WHERE gift_certificate_id=:gift_certificate_id AND tag_id=:tag_id";
	//Incomplete SQL Templates
	private final static String FILTER_BY_TAG_NAME_INCOMPLETE_SQL =
			"INNER JOIN tag_gift_certificate ON gc.id=tag_gift_certificate.gift_certificate_id " +
					"INNER JOIN tag ON tag_gift_certificate.tag_id=tag.id WHERE tag.name=:tag_name";
	private final static String FILTER_BY_NAME_PART_INCOMPLETE_SQL = "POSITION(:name_pattern IN gc.name) > 0";
	private final static String FILTER_BY_DESCRIPTION_PART_INCOMPLETE_SQL =
			"POSITION(:description_pattern IN gc.description) > 0";
	private final static String AND_INCOMPLETE_SQL = "AND";
	private final static String WHERE_INCOMPLETE_SQL = "WHERE";
	private final static String ORDER_BY_INCOMPLETE_SQL = "ORDER BY";
	private final static String ASC_INCOMPLETE_SQL = "ASC";
	private final static String DESC_INCOMPLETE_SQL = "DESC";
	private final static String CERT_TABLE_NAME = "gift_certificate";

	private NamedParameterJdbcOperations jdbcOperations;
	private SimpleJdbcInsert simpleJdbcInsert;

	public JdbcGiftCertificateRepository(NamedParameterJdbcOperations jdbcOperations, DataSource dataSource) {
		this.jdbcOperations = jdbcOperations;
		simpleJdbcInsert =
				new SimpleJdbcInsert(dataSource).withTableName(CERT_TABLE_NAME).usingGeneratedKeyColumns(ID_COLUMN);
	}

	private GiftCertificate mapCertificate(ResultSet rs, int row) throws SQLException {
		return new GiftCertificate(rs.getInt(ID_COLUMN), rs.getString(NAME_COLUMN), rs.getString(DESCRIPTION_COLUMN),
				rs.getBigDecimal(PRICE_COLUMN), rs.getInt(DURATION_COLUMN),
				rs.getTimestamp(CREATE_DATE_COLUMN).toLocalDateTime(),
				rs.getTimestamp(LAST_UPDATE_DATE_COLUMN).toLocalDateTime());
	}

	@Override
	public GiftCertificate createCertificate(GiftCertificate certificate) {
		LocalDateTime localDateTime = LocalDateTime.now(ZoneOffset.UTC);
		Timestamp timestamp = Timestamp.valueOf(localDateTime);
		Map<String, Object> parameters = new HashMap<>();
		parameters.put(NAME_COLUMN, certificate.getName());
		parameters.put(DESCRIPTION_COLUMN, certificate.getDescription());
		parameters.put(PRICE_COLUMN, certificate.getPrice());
		parameters.put(DURATION_COLUMN, certificate.getDuration());
		parameters.put(CREATE_DATE_COLUMN, timestamp);
		parameters.put(LAST_UPDATE_DATE_COLUMN, timestamp);
		int id = simpleJdbcInsert.executeAndReturnKey(parameters).intValue();
		GiftCertificate giftCertificate =
				new GiftCertificate(id, certificate.getName(), certificate.getDescription(), certificate.getPrice(),
						certificate.getDuration(), localDateTime, localDateTime);
		return giftCertificate;
	}

	@Override
	public Optional<GiftCertificate> getCertificateById(int id) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(ID_PARAM_KEY, id);
		return Optional.ofNullable(DataAccessUtils
				.singleResult(jdbcOperations.query(GET_CERTIFICATE_BY_ID_SQL, parameters, this::mapCertificate)));
	}

	@Override
	public List<GiftCertificate> getCertificatesByFilter(Filter filter) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		String sql = buildSqlFromFilter(filter, parameters);
		return jdbcOperations.query(sql, parameters, this::mapCertificate);
	}

	private String buildSqlFromFilter(Filter filter, Map<String, Object> parameters) {
		StringBuilder sb = new StringBuilder(GET_ALL_CERTIFICATES_SQL);
		String joiner = WHERE_INCOMPLETE_SQL;
		if (filter.getTagName() != null) {
			sb.append(' ').append(FILTER_BY_TAG_NAME_INCOMPLETE_SQL);
			parameters.put(TAG_NAME_PARAM_KEY, filter.getTagName());
			joiner = AND_INCOMPLETE_SQL;
		}
		if (filter.getNamePart() != null && !filter.getNamePart().isBlank()) {
			sb.append(' ').append(joiner).append(' ').append(FILTER_BY_NAME_PART_INCOMPLETE_SQL);
			parameters.put(NAME_PATTERN_PARAM_KEY, filter.getNamePart());
			joiner = AND_INCOMPLETE_SQL;
		}
		if (filter.getDescriptionPart() != null && !filter.getDescriptionPart().isBlank()) {
			sb.append(' ').append(joiner).append(' ').append(FILTER_BY_DESCRIPTION_PART_INCOMPLETE_SQL);
			parameters.put(DESCRIPTION_PATTERN_PARAM_KEY, filter.getDescriptionPart());
			joiner = AND_INCOMPLETE_SQL;
		}
		if (filter.getSortBy() != null) {
			sb.append(' ').append(buildOrderBy(filter.getSortBy()));
		}
		return sb.toString();
	}

	private String buildOrderBy(SortOption sortBy) {
		StringBuilder sb = new StringBuilder(ORDER_BY_INCOMPLETE_SQL);
		sb.append(' ');
		switch (sortBy.getField()) {
			case NAME -> sb.append(NAME_COLUMN);
			case CREATE_DATE -> sb.append(CREATE_DATE_COLUMN);
			case LAST_UPDATE_DATE -> sb.append(LAST_UPDATE_DATE_COLUMN);
		}
		sb.append(' ');
		switch (sortBy.getDirection()) {
			case ASC -> sb.append(ASC_INCOMPLETE_SQL);
			case DESC -> sb.append(DESC_INCOMPLETE_SQL);
		}
		return sb.toString();
	}


	@Override
	public boolean updateCertificate(GiftCertificate certificate) {
		LocalDateTime localDateTime = LocalDateTime.now(ZoneOffset.UTC);
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(ID_PARAM_KEY, certificate.getId());
		parameters.put(NAME_PARAM_KEY, certificate.getName());
		parameters.put(DESCRIPTION_PARAM_KEY, certificate.getDescription());
		parameters.put(DURATION_PARAM_KEY, certificate.getDuration());
		parameters.put(PRICE_PARAM_KEY, certificate.getPrice());
		parameters.put(LAST_UPDATE_DATE_PARAM_KEY, localDateTime);
		return jdbcOperations.update(UPDATE_CERTIFICATE_SQL, parameters) > 0;
	}

	@Override
	public boolean deleteCertificate(int id) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(ID_PARAM_KEY, id);
		return jdbcOperations.update(DELETE_CERTIFICATE_SQL, parameters) > 0;
	}

	@Override
	public void addTagToCertificate(int certificateId, int tagId) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(TAG_ID_PARAM_KEY, tagId);
		parameters.put(GIFT_CERTIFICATE_ID_PARAM_KEY, certificateId);
		jdbcOperations.update(ADD_TAG_TO_CERTIFICATE_SQL, parameters);
	}

	@Override
	public void removeTagFromCertificate(int certificateId, int tagId) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(TAG_ID_PARAM_KEY, tagId);
		parameters.put(GIFT_CERTIFICATE_ID_PARAM_KEY, certificateId);
		jdbcOperations.update(DELETE_TAG_FROM_CERTIFICATE_SQL, parameters);
	}
}
