package com.epam.esm.service.converter;

import com.epam.esm.model.dto.GiftCertificateSearchFilterDTO;
import com.epam.esm.model.entity.GiftCertificateSearchFilter;
import com.epam.esm.model.entity.SortOption;
import com.epam.esm.service.exception.InvalidCertificateException;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mapstruct.factory.Mappers;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestInstance(PER_CLASS)
public class GiftCertificateSearchGiftCertificateSearchFilterConverterTest {
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
	private static GiftCertificateSearchFilterConverter
			giftCertificateSearchFilterConverter = Mappers.getMapper(GiftCertificateSearchFilterConverter.class);

	private Stream<GiftCertificateSearchFilterDTO> correctTestSources() {
		return Stream.of(GiftCertificateSearchFilterDTO.builder().namePart(NAME_PART).descriptionPart(DESC_PART).tagNames(TAG_NAMES)
						.sortBy(CORRECT_SORT_OPTION_STR).build(),
				GiftCertificateSearchFilterDTO.builder().namePart(NAME_PART).build(),
				GiftCertificateSearchFilterDTO.builder().descriptionPart(DESC_PART).build(),
				GiftCertificateSearchFilterDTO.builder().tagNames(TAG_NAMES).build(),
				GiftCertificateSearchFilterDTO.builder().sortBy(CORRECT_SORT_OPTION_STR).build());
	}

	@ParameterizedTest
	@MethodSource("correctTestSources")
	public void convertShouldReturnFilterWhenPassedCorrectFilterDto(GiftCertificateSearchFilterDTO dto) {
		assertDoesNotThrow(() -> {
			GiftCertificateSearchFilter giftCertificateSearchFilter = giftCertificateSearchFilterConverter.convert(dto);
			assertEquals(dto.getNamePart(), giftCertificateSearchFilter.getNamePart());
			assertEquals(dto.getDescriptionPart(), giftCertificateSearchFilter.getDescriptionPart());
			assertEquals(dto.getTagNames(), giftCertificateSearchFilter.getTagNames());
			if (dto.getSortBy() != null) {
				assertEquals(EXPECTED_SORT_OPTION, giftCertificateSearchFilter.getSortBy());
			}
		});
	}

	private Stream<Arguments> incorrectTestSources() {
		Random random = new Random();
		return Stream.of(
				Arguments.of(GiftCertificateSearchFilterDTO.builder().sortBy(INVALID_SORT_NUMBER_OF_TOKENS_STR).build(),
						INVALID_NUMBER_OF_TOKENS_TEMPLATE,
						TEST_EXCEPTION_MESSAGE_TEMPLATE + random.nextInt()),
				Arguments.of(GiftCertificateSearchFilterDTO.builder().sortBy(INVALID_SORT_DIRECTION_STR).build(),
						INVALID_DIRECTION_TOKEN_TEMPLATE,
						TEST_EXCEPTION_MESSAGE_TEMPLATE + random.nextInt()),
				Arguments.of(GiftCertificateSearchFilterDTO.builder().sortBy(INVALID_SORT_FIELD_STR).build(),
						INVALID_FIELD_TOKEN_TEMPLATE,
						TEST_EXCEPTION_MESSAGE_TEMPLATE + random.nextInt()));
	}

	@ParameterizedTest
	@MethodSource("incorrectTestSources")
	public void convertShouldThrowExceptionWhenPassedFilterDtoWithInvalidSortOption(GiftCertificateSearchFilterDTO dto,
	                                                                                String fieldName,
	                                                                                String fieldValue) {
		ReflectionTestUtils.setField(giftCertificateSearchFilterConverter, fieldName, fieldValue, String.class);
		InvalidCertificateException ex = assertThrows(InvalidCertificateException.class,
				() -> giftCertificateSearchFilterConverter.convert(dto));
		assertEquals(InvalidCertificateException.Reason.INVALID_SORT_BY, ex.getReason());
	}
}
