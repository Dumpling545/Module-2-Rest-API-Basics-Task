package com.epam.esm.service.converter;

import com.epam.esm.db.TagRepository;
import com.epam.esm.model.dto.FilterDTO;
import com.epam.esm.model.entity.Filter;
import com.epam.esm.model.entity.SortOption;
import com.epam.esm.model.entity.Tag;
import com.epam.esm.service.exception.InvalidCertificateException;
import com.epam.esm.service.exception.InvalidTagException;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Value;

import java.util.Optional;
import java.util.Set;

@Mapper(componentModel="spring")
public abstract class FilterConverter {

	private static final String DELIMITER = " ";
	private static final int REQUIRED_SORT_BY_NUMBER_OF_TOKENS = 2;
	private static final int FIELD_TOKEN_INDEX = 0;
	private static final int DIRECTION_TOKEN_INDEX = 1;

	@Value("${cert.exception.sort-by.invalid-direction}")
	private String invalidDirectionTokenTemplate;
	@Value("${cert.exception.sort-by.invalid-field}")
	private String invalidFieldTokenTemplate;
	@Value("${cert.exception.sort-by.invalid-number-of-tokens}")
	private String invalidNumberOfTokensTemplate;

	@Mapping(target = "sortBy", source = "dto.sortBy")
	public abstract Filter convert(FilterDTO dto, Set<Tag> tags);

	public SortOption convert(String sortBy){
		SortOption sortOption = null;
		if (sortBy != null) {
			String tokens[] = sortBy.split(DELIMITER);
			if (tokens.length != REQUIRED_SORT_BY_NUMBER_OF_TOKENS) {
				String message = String.format(invalidNumberOfTokensTemplate, sortBy, tokens.length,
						String.join(DELIMITER, tokens), REQUIRED_SORT_BY_NUMBER_OF_TOKENS);
				throw new InvalidCertificateException(message, InvalidCertificateException.Reason.INVALID_SORT_BY);
			}
			SortOption.Field field = field(tokens[FIELD_TOKEN_INDEX]);
			SortOption.Direction direction = direction(tokens[DIRECTION_TOKEN_INDEX]);
			sortOption = new SortOption(field, direction);
		}
		return sortOption;
	}

	private SortOption.Field field(String field) {
		try {
			return SortOption.Field.valueOf(field.toUpperCase());
		} catch (IllegalArgumentException | NullPointerException ex) {
			String message = String.format(invalidFieldTokenTemplate, field);
			throw new InvalidCertificateException(message, ex, InvalidCertificateException.Reason.INVALID_SORT_BY);
		}
	}

	private SortOption.Direction direction(String direction) {
		try {
			return SortOption.Direction.valueOf(direction.toUpperCase());
		} catch (IllegalArgumentException | NullPointerException ex) {
			String message = String.format(invalidDirectionTokenTemplate, direction);
			throw new InvalidCertificateException(message, ex, InvalidCertificateException.Reason.INVALID_SORT_BY);
		}
	}

}
