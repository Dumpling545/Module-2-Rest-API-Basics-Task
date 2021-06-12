package com.epam.esm.service.converter.impl;

import com.epam.esm.model.dto.FilterDTO;
import com.epam.esm.model.entity.Filter;
import com.epam.esm.model.entity.SortOption;
import com.epam.esm.service.converter.Converter;
import com.epam.esm.service.exception.InvalidCertificateSortByOptionException;
import com.epam.esm.service.exception.ServiceException;
import org.springframework.stereotype.Component;

@Component
public class FilterDtoToFilterConverter implements Converter<FilterDTO, Filter> {
	private static final String DELIMITER = " ";
	private static final int REQUIRED_SORT_BY_NUMBER_OF_TOKENS = 2;
	private static final String INVALID_NUMBER_OF_TOKENS_TEMPLATE =
			"SortBy option %s splits in %d tokens: %s. Required number of tokens is " +
					REQUIRED_SORT_BY_NUMBER_OF_TOKENS;

	private static final int FIELD_TOKEN_INDEX = 0;
	private static final String INVALID_FIELD_TOKEN_TEMPLATE = "Field '%s' is invalid";

	private static final int DIRECTION_TOKEN_INDEX = 1;
	private static final String INVALID_DIRECTION_TOKEN_TEMPLATE = "Direction '%s' is invalid";

	private SortOption.Field field(String field) {
		SortOption.Field fieldEnum = null;
		try {
			SortOption.Field.valueOf(field.toUpperCase());
		} catch (IllegalArgumentException | NullPointerException ex) {
			String message = String.format(INVALID_FIELD_TOKEN_TEMPLATE, field);
			throw new InvalidCertificateSortByOptionException(message, ex);
		}
		return fieldEnum;
	}

	private SortOption.Direction direction(String direction) {
		SortOption.Direction directionEnum = null;
		try {
			SortOption.Direction.valueOf(direction.toUpperCase());
		} catch (IllegalArgumentException | NullPointerException ex) {
			String message = String.format(INVALID_DIRECTION_TOKEN_TEMPLATE, direction);
			throw new InvalidCertificateSortByOptionException(message, ex);
		}
		return directionEnum;
	}

	@Override
	public Filter convert(FilterDTO filterDTO) {
		String tokens[] = filterDTO.getSortBy().split(DELIMITER);
		if (tokens.length != REQUIRED_SORT_BY_NUMBER_OF_TOKENS) {
			String message = String.format(INVALID_NUMBER_OF_TOKENS_TEMPLATE, filterDTO.getSortBy(), tokens.length,
					String.join(DELIMITER, tokens));
			throw new InvalidCertificateSortByOptionException(message);
		}
		SortOption.Field field = field(tokens[FIELD_TOKEN_INDEX]);
		SortOption.Direction direction = direction(tokens[DIRECTION_TOKEN_INDEX]);
		SortOption sortOption = new SortOption(field, direction);
		Filter filter =
				new Filter(filterDTO.getNamePart(), filterDTO.getDescriptionPart(), filterDTO.getTagName(), sortOption);
		return filter;
	}

}
