package com.epam.esm.db.impl;

import com.epam.esm.db.GiftCertificateRepository;
import com.epam.esm.model.entity.Filter;
import com.epam.esm.model.entity.GiftCertificate;
import com.epam.esm.model.entity.SortOption;
import org.springframework.beans.factory.annotation.Autowired;
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
	@Value("${cert.sql.table.name}")
	private String certificateTable;
	//SQL Table Column Labels
	@Value("${cert.sql.column.id}")
	private String idColumn;
	@Value("${cert.sql.column.name}")
	private String nameColumn;
	@Value("${cert.sql.column.description}")
	private String descriptionColumn;
	@Value("${cert.sql.column.duration}")
	private String durationColumn;
	@Value("${cert.sql.column.price}")
	private String priceColumn;
	@Value("${cert.sql.column.create-date}")
	private String createDateColumn;
	@Value("${cert.sql.column.last-update-date}")
	private String lastUpdateDateColumn;
	//SQL Parameter Keys
	@Value("${cert.sql.param-key.id}")
	private String idParamKey;
	@Value("${cert.sql.param-key.gift-certificate-id}")
	private String giftCertificateIdParamKey;
	@Value("${cert.sql.param-key.name}")
	private String nameParamKey;
	@Value("${cert.sql.param-key.name-pattern}")
	private String namePatternParamKey;
	@Value("${cert.sql.param-key.description}")
	private String descriptionParamKey;
	@Value("${cert.sql.param-key.description-pattern}")
	private String descriptionPatternParamKey;
	@Value("${cert.sql.param-key.duration}")
	private String durationParamKey;
	@Value("${cert.sql.param-key.price}")
	private String priceParamKey;
	@Value("${cert.sql.param-key.last-update-date}")
	private String lastUpdateDateParamKey;
	@Value("${cert.sql.param-key.tag-id}")
	private String tagIdParamKey;
	@Value("${cert.sql.param-key.tag-name}")
	private String tagNameParamKey;
	//SQL Template Queries
	@Value("${cert.sql.query.get-all}")
	private String getAllCertificatesSql;
	@Value("${cert.sql.query.get-by-id}")
	private String getCertificateByIdSql;
	@Value("${cert.sql.query.update}")
	private String updateCertificateSql;
	@Value("${cert.sql.query.delete}")
	private String deleteCertificateSql;
	@Value("${cert.sql.query.add-tag}")
	private String addTagToCertificateSql;
	@Value("${cert.sql.query.delete-tag}")
	private String deleteTagFromCertificateSql;
	//Incomplete SQL Templates
	@Value("${cert.sql.query-part.filter-by-tag-name}")
	private String filterByTagNameIncompleteSql;
	@Value("${cert.sql.query-part.filter-by-name-part}")
	private String filterByNamePartIncompleteSql;
	@Value("${cert.sql.query-part.filter-by-description-part}")
	private String filterByDescriptionPartIncompleteSql;
	@Value("${cert.sql.query-part.and}")
	private String andIncompleteSql;
	@Value("${cert.sql.query-part.where}")
	private String whereIncompleteSql;
	@Value("${cert.sql.query-part.order-by}")
	private String orderByIncompleteSql;
	@Value("${cert.sql.query-part.asc}")
	private String ascIncompleteSql;
	@Value("${cert.sql.query-part.desc}")
	private String descIncompleteSql;

	private final NamedParameterJdbcOperations jdbcOperations;
	private final SimpleJdbcInsert simpleJdbcInsert;

	@Autowired
	public JdbcGiftCertificateRepository(NamedParameterJdbcOperations jdbcOperations, DataSource dataSource) {
		this.jdbcOperations = jdbcOperations;
		simpleJdbcInsert =
				new SimpleJdbcInsert(dataSource).withTableName(certificateTable).usingGeneratedKeyColumns(idColumn);
	}

	private GiftCertificate mapCertificate(ResultSet rs, int row) throws SQLException {
		return new GiftCertificate(rs.getInt(idColumn), rs.getString(nameColumn), rs.getString(descriptionColumn),
				rs.getDouble(priceColumn), rs.getInt(durationColumn),
				rs.getTimestamp(createDateColumn).toLocalDateTime(),
				rs.getTimestamp(lastUpdateDateColumn).toLocalDateTime());
	}

	@Override
	public void createCertificate(GiftCertificate certificate) {
		LocalDateTime localDateTime = LocalDateTime.now(ZoneOffset.UTC);
		Timestamp timestamp = Timestamp.valueOf(localDateTime);
		Map<String, Object> parameters = new HashMap<>(6);
		parameters.put(nameColumn, certificate.getName());
		parameters.put(descriptionColumn, certificate.getDescription());
		parameters.put(priceColumn, certificate.getPrice());
		parameters.put(durationColumn, certificate.getDuration());
		parameters.put(createDateColumn, timestamp);
		parameters.put(lastUpdateDateColumn, timestamp);
		int id = simpleJdbcInsert.executeAndReturnKey(parameters).intValue();
	}

	@Override
	public Optional<GiftCertificate> getCertificateById(int id) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(idParamKey, id);
		return Optional.ofNullable(DataAccessUtils
				.singleResult(jdbcOperations.query(getCertificateByIdSql, parameters, this::mapCertificate)));
	}

	@Override
	public List<GiftCertificate> getCertificatesByFilter(Filter filter) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		String sql = buildSqlFromFilter(filter, parameters);
		return jdbcOperations.query(sql, parameters, this::mapCertificate);
	}

	private String buildSqlFromFilter(Filter filter, Map<String, Object> parameters) {
		StringBuilder sb = new StringBuilder(getAllCertificatesSql);
		String joiner = whereIncompleteSql;
		if (filter.getTagName() != null) {
			sb.append(' ').append(filterByTagNameIncompleteSql);
			parameters.put(tagNameParamKey, filter.getTagName());
			joiner = andIncompleteSql;
		}
		if (filter.getNamePart() != null && !filter.getNamePart().isBlank()) {
			sb.append(' ').append(joiner).append(' ').append(filter.getNamePart());
			parameters.put(namePatternParamKey, "%" + filter.getNamePart() + "%");
			joiner = andIncompleteSql;
		}
		if (filter.getDescriptionPart() != null && !filter.getDescriptionPart().isBlank()) {
			sb.append(' ').append(joiner).append(' ').append(filter.getDescriptionPart());
			parameters.put(descriptionPatternParamKey, "%" + filter.getDescriptionPart() + "%");
			joiner = andIncompleteSql;
		}
		if (filter.getSortBy() != null) {
			sb.append(' ').append(buildOrderBy(filter.getSortBy()));
		}
		return sb.toString();
	}

	private String buildOrderBy(SortOption sortBy) {
		StringBuilder sb = new StringBuilder(orderByIncompleteSql);
		sb.append(' ');
		switch (sortBy.getField()) {
			case NAME -> sb.append(nameColumn);
			case CREATE_DATE -> sb.append(createDateColumn);
			case LAST_UPDATE_DATE -> sb.append(lastUpdateDateColumn);
		}
		sb.append(' ');
		switch (sortBy.getDirection()) {
			case ASC -> sb.append(ascIncompleteSql);
			case DESC -> sb.append(descIncompleteSql);
		}
		return sb.toString();
	}


	@Override
	public boolean updateCertificate(GiftCertificate certificate) {
		LocalDateTime localDateTime = LocalDateTime.now(ZoneOffset.UTC);
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(idParamKey, certificate.getId());
		parameters.put(nameParamKey, certificate.getName());
		parameters.put(descriptionParamKey, certificate.getDescription());
		parameters.put(durationParamKey, certificate.getDuration());
		parameters.put(priceParamKey, certificate.getPrice());
		parameters.put(lastUpdateDateParamKey, localDateTime);
		return jdbcOperations.update(updateCertificateSql, parameters) > 0;
	}

	@Override
	public boolean deleteCertificate(int id) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(idParamKey, id);
		return jdbcOperations.update(deleteCertificateSql, parameters) > 0;
	}

	@Override
	public void addTag(int certificateId, int tagId) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(tagIdParamKey, tagId);
		parameters.put(giftCertificateIdParamKey, certificateId);
		jdbcOperations.update(addTagToCertificateSql, parameters);
	}

	@Override
	public void removeTag(int certificateId, int tagId) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(tagIdParamKey, tagId);
		parameters.put(giftCertificateIdParamKey, certificateId);
		jdbcOperations.update(deleteTagFromCertificateSql, parameters);
	}
}
