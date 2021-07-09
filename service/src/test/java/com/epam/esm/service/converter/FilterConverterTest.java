package com.epam.esm.service.converter;

import com.epam.esm.model.dto.FilterDTO;
import com.epam.esm.model.dto.TagDTO;
import com.epam.esm.model.entity.Filter;
import com.epam.esm.model.entity.SortOption;
import com.epam.esm.service.converter.FilterConverter;
import com.epam.esm.service.exception.InvalidCertificateException;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.shadow.com.univocity.parsers.conversions.Validator;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestInstance(PER_CLASS)
public class FilterConverterTest {
	private static final Set<String> TAG_NAMES = Set.of("tag1", "tag2");
	private static final String NAME_PART = "part1";
	private static final String DESC_PART = "part2";
	private static final String CORRECT_SORT_OPTION_STR = "name desc";
	private static final String INVALID_SORT_NUMBER_OF_TOKENS_STR = "name";
	private static final String INVALID_SORT_FIELD_STR = "price desc";
	private static final String INVALID_SORT_DIRECTION_STR = "name ascdesc";
	private static final SortOption EXPECTED_SORT_OPTION = new SortOption(SortOption.Field.NAME,
			SortOption.Direction.DESC);
	private static final String TEST_EXCEPTION_MESSAGE_TEMPLATE = "Test message %s";
	private static final String INVALID_NUMBER_OF_TOKENS_TEMPLATE = "invalidNumberOfTokensTemplate";
	private static final String INVALID_DIRECTION_TOKEN_TEMPLATE = "invalidDirectionTokenTemplate";
	private static final String INVALID_FIELD_TOKEN_TEMPLATE = "invalidFieldTokenTemplate";
	private static FilterConverter filterConverter = Mappers.getMapper(FilterConverter.class);

	private Stream<FilterDTO> correctTestSources() {
		return Stream.of(FilterDTO.builder().namePart(NAME_PART).descriptionPart(DESC_PART).tagNames(TAG_NAMES)
						.sortBy(CORRECT_SORT_OPTION_STR).build(),
				FilterDTO.builder().namePart(NAME_PART).build(),
				FilterDTO.builder().descriptionPart(DESC_PART).build(),
				FilterDTO.builder().tagNames(TAG_NAMES).build(),
				FilterDTO.builder().sortBy(CORRECT_SORT_OPTION_STR).build());
	}

	@ParameterizedTest
	@MethodSource("correctTestSources")
	public void convertShouldReturnFilterWhenPassedCorrectFilterDto(FilterDTO dto) {
		assertDoesNotThrow(() -> {
			Filter filter = filterConverter.convert(dto);
			assertEquals(dto.getNamePart(), filter.getNamePart());
			assertEquals(dto.getDescriptionPart(), filter.getDescriptionPart());
			assertEquals(dto.getTagNames(), filter.getTagNames());
			if (dto.getSortBy() != null) {
				assertEquals(EXPECTED_SORT_OPTION, filter.getSortBy());
			}
		});
	}

	private Stream<Arguments> incorrectTestSources() {
		Random random = new Random();
		return Stream.of(
				Arguments.of(FilterDTO.builder().sortBy(INVALID_SORT_NUMBER_OF_TOKENS_STR).build(),
						INVALID_NUMBER_OF_TOKENS_TEMPLATE,
						TEST_EXCEPTION_MESSAGE_TEMPLATE + random.nextInt()),
				Arguments.of(FilterDTO.builder().sortBy(INVALID_SORT_DIRECTION_STR).build(),
						INVALID_DIRECTION_TOKEN_TEMPLATE,
						TEST_EXCEPTION_MESSAGE_TEMPLATE + random.nextInt()),
				Arguments.of(FilterDTO.builder().sortBy(INVALID_SORT_FIELD_STR).build(),
						INVALID_FIELD_TOKEN_TEMPLATE,
						TEST_EXCEPTION_MESSAGE_TEMPLATE + random.nextInt()));
	}

	@ParameterizedTest
	@MethodSource("incorrectTestSources")
	public void convertShouldThrowExceptionWhenPassedFilterDtoWithInvalidSortOption(FilterDTO dto,
	                                                                                String fieldName,
	                                                                                String fieldValue) {
		ReflectionTestUtils.setField(filterConverter, fieldName, fieldValue, String.class);
		InvalidCertificateException ex = assertThrows(InvalidCertificateException.class,
				() -> filterConverter.convert(dto));
		assertEquals(InvalidCertificateException.Reason.INVALID_SORT_BY, ex.getReason());
	}
}
