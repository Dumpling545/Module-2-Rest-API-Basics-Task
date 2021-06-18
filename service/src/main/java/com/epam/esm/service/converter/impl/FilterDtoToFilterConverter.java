package com.epam.esm.service.converter.impl;

import com.epam.esm.model.dto.FilterDTO;
import com.epam.esm.model.dto.TagDTO;
import com.epam.esm.model.entity.Filter;
import com.epam.esm.model.entity.SortOption;
import com.epam.esm.service.converter.Converter;
import com.epam.esm.service.exception.InvalidCertificateException;
import com.epam.esm.service.validator.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FilterDtoToFilterConverter implements Converter<FilterDTO, Filter> {
	private static final String DELIMITER = " ";
	private static final int REQUIRED_SORT_BY_NUMBER_OF_TOKENS = 2;
	private static final int FIELD_TOKEN_INDEX = 0;
	private static final int DIRECTION_TOKEN_INDEX = 1;
	private static final int DEFAULT_ID = -1;

	@Value("${cert.exception.sort-by.invalid-direction}")
	private String invalidDirectionTokenTemplate;
	@Value("${cert.exception.sort-by.invalid-field}")
	private String invalidFieldTokenTemplate;
	@Value("${cert.exception.sort-by.invalid-number-of-tokens}")
	private String invalidNumberOfTokensTemplate;

	private Validator<TagDTO> tagValidator;

	public FilterDtoToFilterConverter(Validator<TagDTO> tagValidator) {
		this.tagValidator = tagValidator;
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

	@Override
	public Filter convert(FilterDTO filterDTO) {
		SortOption sortOption = null;
		if (filterDTO.getSortBy() != null) {
			String tokens[] = filterDTO.getSortBy().split(DELIMITER);
			if (tokens.length != REQUIRED_SORT_BY_NUMBER_OF_TOKENS) {
				String message = String.format(invalidNumberOfTokensTemplate, filterDTO.getSortBy(), tokens.length,
						String.join(DELIMITER, tokens), REQUIRED_SORT_BY_NUMBER_OF_TOKENS);
				throw new InvalidCertificateException(message, InvalidCertificateException.Reason.INVALID_SORT_BY);
			}
			SortOption.Field field = field(tokens[FIELD_TOKEN_INDEX]);
			SortOption.Direction direction = direction(tokens[DIRECTION_TOKEN_INDEX]);
			sortOption = new SortOption(field, direction);
		}
		if (filterDTO.getTagName() != null) {
			tagValidator.validate(new TagDTO(DEFAULT_ID, filterDTO.getTagName()));
		}
		Filter filter = new Filter(filterDTO.getNamePart(), filterDTO.getDescriptionPart(), filterDTO.getTagName(),
				sortOption);
		return filter;
	}

}
