package com.epam.esm.service.converter.impl;

import com.epam.esm.model.dto.FilterDTO;
import com.epam.esm.model.dto.TagDTO;
import com.epam.esm.model.entity.Filter;
import com.epam.esm.model.entity.SortOption;
import com.epam.esm.service.converter.Converter;
import com.epam.esm.service.exception.InvalidCertificateException;
import com.epam.esm.service.exception.InvalidTagException;
import com.epam.esm.service.validator.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestInstance(PER_CLASS)
public class FilterDtoToFilterConverterTest {
	private static final String TAG_NAME = "tag";
	private static final String NAME_PART = "part1";
	private static final String DESC_PART = "part2";
	private static final String CORRECT_SORT_OPTION_STR = "name desc";
	private static final String INVALID_SORT_OPTION_STR = "price desc";
	private static final SortOption EXPECTED_SORT_OPTION =
			new SortOption(SortOption.Field.NAME, SortOption.Direction.DESC);
	private static final String TEST_EXCEPTION_MESSAGE_TEMPLATE = "Test message %s";
	private static final String EXPECTED_SORT_OPTION_MESSAGE = "Test message price";
	private static final String INVALID_FIELD_TOKEN_TEMPLATE_FIELD_NAME = "invalidFieldTokenTemplate";

	private Stream<FilterDTO> correctTestSources() {

		return Stream.of(new FilterDTO(NAME_PART, DESC_PART, TAG_NAME, CORRECT_SORT_OPTION_STR),
				new FilterDTO(NAME_PART, null, null, null), new FilterDTO(null, DESC_PART, null, null),
				new FilterDTO(null, null, TAG_NAME, null), new FilterDTO(null, null, null, CORRECT_SORT_OPTION_STR));
	}

	@ParameterizedTest
	@MethodSource("correctTestSources")
	public void convertShouldReturnFilterWhenPassedCorrectFilterDto(FilterDTO dto) {
		Validator<TagDTO> tagValidator = Mockito.mock(Validator.class);
		Converter<FilterDTO, Filter> converter = new FilterDtoToFilterConverter(tagValidator);
		assertDoesNotThrow(() -> {
			Filter filter = converter.convert(dto);
			assertEquals(dto.getNamePart(), filter.getNamePart());
			assertEquals(dto.getDescriptionPart(), filter.getDescriptionPart());
			assertEquals(dto.getTagName(), filter.getTagName());
			if (dto.getSortBy() != null) {
				assertEquals(EXPECTED_SORT_OPTION, filter.getSortBy());
			}
		});
	}

	@Test
	public void convertShouldThrowExceptionWhenPassedFilterDtoWithInvalidTagName() {
		Validator<TagDTO> tagValidator = Mockito.mock(Validator.class);
		Mockito.doThrow(InvalidTagException.class).when(tagValidator).validate(Mockito.any());
		FilterDTO dto = new FilterDTO(NAME_PART, DESC_PART, TAG_NAME, CORRECT_SORT_OPTION_STR);
		Converter<FilterDTO, Filter> converter = new FilterDtoToFilterConverter(tagValidator);
		assertThrows(InvalidTagException.class, () -> converter.convert(dto));
	}

	@Test
	public void convertShouldThrowExceptionWhenPassedFilterDtoWithInvalidSortOption() {
		Validator<TagDTO> tagValidator = Mockito.mock(Validator.class);
		FilterDTO dto = new FilterDTO(NAME_PART, DESC_PART, TAG_NAME, INVALID_SORT_OPTION_STR);
		Converter<FilterDTO, Filter> converter = new FilterDtoToFilterConverter(tagValidator);
		ReflectionTestUtils
				.setField(converter, INVALID_FIELD_TOKEN_TEMPLATE_FIELD_NAME, TEST_EXCEPTION_MESSAGE_TEMPLATE,
						String.class);
		InvalidCertificateException ex = assertThrows(InvalidCertificateException.class, () -> converter.convert(dto));
		assertEquals(InvalidCertificateException.Reason.INVALID_SORT_BY, ex.getReason());
		assertEquals(EXPECTED_SORT_OPTION_MESSAGE, ex.getMessage());
	}
}
