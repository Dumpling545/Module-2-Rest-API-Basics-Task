package com.epam.esm.service.validator.impl;

import com.epam.esm.model.dto.GiftCertificateCreateDTO;
import com.epam.esm.service.exception.InvalidCertificateException;
import com.epam.esm.service.validator.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestInstance(Lifecycle.PER_CLASS)
public class GiftCertificateCreateDtoValidatorTest {
	static final int MIN_NAME_LENGTH = 3;
	private static final int MAX_NAME_LENGTH = 10;
	private static final int MIN_DESC_LENGTH = 5;
	private static final int MAX_DESC_LENGTH = 12;
	private static final int MIN_DURATION = 1;
	private static final int MAX_DURATION = 10;
	private static final double MIN_PRICE = 0.1;
	private static final double MAX_PRICE = 24.32;
	private static final int CORRECT_DURATION = 5;
	private static final int DELTA_DURATION = 1;
	private static final double CORRECT_PRICE = 15.46;
	private static final double DELTA_PRICE = 1;
	private static final String CORRECT_NAME = "abcde";
	private static final String TOO_SHORT_NAME = "a";
	private static final String TOO_LONG_NAME = "abcdefghijklmnop";
	private static final String CORRECT_DESC = "abcdefghijk";
	private static final String TOO_SHORT_DESC = "a";
	private static final String TOO_LONG_DESC = "abcdefghijklmnopqrstu";
	private static final String MIN_NAME_LENGTH_FIELD_NAME = "minNameLength";
	private static final String MAX_NAME_LENGTH_FIELD_NAME = "maxNameLength";
	private static final String MIN_DESC_LENGTH_FIELD_NAME = "minDescLength";
	private static final String MAX_DESC_LENGTH_FIELD_NAME = "maxDescLength";
	private static final String MIN_DURATION_FIELD_NAME = "minDuration";
	private static final String MAX_DURATION_FIELD_NAME = "maxDuration";
	private static final String MIN_PRICE_FIELD_NAME = "minPrice";
	private static final String MAX_PRICE_FIELD_NAME = "maxPrice";
	private static final String NULL_NAME_MESSAGE_FIELD_NAME = "nullNameMessage";
	private static final String OUT_OF_BOUNDS_NAME_MESSAGE_FIELD_NAME = "outOfBoundsNameTemplate";
	private static final String NULL_DESC_MESSAGE_FIELD_NAME = "nullDescMessage";
	private static final String OUT_OF_BOUNDS_DESC_MESSAGE_FIELD_NAME = "outOfBoundsDescTemplate";
	private static final String NULL_DURATION_MESSAGE_FIELD_NAME = "nullDurationMessage";
	private static final String OUT_OF_BOUNDS_DURATION_MESSAGE_FIELD_NAME = "outOfBoundsDurationTemplate";
	private static final String NULL_PRICE_MESSAGE_FIELD_NAME = "nullPriceMessage";
	private static final String OUT_OF_BOUNDS_PRICE_MESSAGE_FIELD_NAME = "outOfBoundsPriceTemplate";
	private static final String MOCK_EX_MESSAGE = "test";
	private Validator<GiftCertificateCreateDTO> validator = new GiftCertificateCreateDtoValidatorImpl();

	@BeforeAll
	public void initValidator() {
		ReflectionTestUtils.setField(validator, MIN_NAME_LENGTH_FIELD_NAME, MIN_NAME_LENGTH);
		ReflectionTestUtils.setField(validator, MAX_NAME_LENGTH_FIELD_NAME, MAX_NAME_LENGTH);
		ReflectionTestUtils.setField(validator, MIN_DESC_LENGTH_FIELD_NAME, MIN_DESC_LENGTH);
		ReflectionTestUtils.setField(validator, MAX_DESC_LENGTH_FIELD_NAME, MAX_DESC_LENGTH);
		ReflectionTestUtils.setField(validator, MIN_DURATION_FIELD_NAME, MIN_DURATION);
		ReflectionTestUtils.setField(validator, MAX_DURATION_FIELD_NAME, MAX_DURATION);
		ReflectionTestUtils.setField(validator, MIN_PRICE_FIELD_NAME, MIN_PRICE);
		ReflectionTestUtils.setField(validator, MAX_PRICE_FIELD_NAME, MAX_PRICE);
		ReflectionTestUtils.setField(validator, NULL_NAME_MESSAGE_FIELD_NAME, MOCK_EX_MESSAGE, String.class);
		ReflectionTestUtils.setField(validator, NULL_DESC_MESSAGE_FIELD_NAME, MOCK_EX_MESSAGE, String.class);
		ReflectionTestUtils.setField(validator, NULL_DURATION_MESSAGE_FIELD_NAME, MOCK_EX_MESSAGE, String.class);
		ReflectionTestUtils.setField(validator, NULL_PRICE_MESSAGE_FIELD_NAME, MOCK_EX_MESSAGE, String.class);
		ReflectionTestUtils.setField(validator, OUT_OF_BOUNDS_NAME_MESSAGE_FIELD_NAME, MOCK_EX_MESSAGE, String.class);
		ReflectionTestUtils.setField(validator, OUT_OF_BOUNDS_DESC_MESSAGE_FIELD_NAME, MOCK_EX_MESSAGE, String.class);
		ReflectionTestUtils
				.setField(validator, OUT_OF_BOUNDS_DURATION_MESSAGE_FIELD_NAME, MOCK_EX_MESSAGE, String.class);
		ReflectionTestUtils.setField(validator, OUT_OF_BOUNDS_PRICE_MESSAGE_FIELD_NAME, MOCK_EX_MESSAGE, String.class);

	}

	private Stream<GiftCertificateCreateDTO> invalidNameTestSource() {
		List<GiftCertificateCreateDTO> tests = new ArrayList<>();
		tests.add(new GiftCertificateCreateDTO(TOO_SHORT_NAME, CORRECT_DESC, CORRECT_PRICE, CORRECT_DURATION,
				Collections.EMPTY_SET));
		tests.add(new GiftCertificateCreateDTO(TOO_LONG_NAME, CORRECT_DESC, CORRECT_PRICE, CORRECT_DURATION,
				Collections.EMPTY_SET));
		tests.add(new GiftCertificateCreateDTO(null, CORRECT_DESC, CORRECT_PRICE, CORRECT_DURATION,
				Collections.EMPTY_SET));
		return tests.stream();
	}

	private Stream<GiftCertificateCreateDTO> invalidDescriptionTestSource() {
		List<GiftCertificateCreateDTO> tests = new ArrayList<>();
		tests.add(new GiftCertificateCreateDTO(CORRECT_NAME, TOO_SHORT_DESC, CORRECT_PRICE, CORRECT_DURATION,
				Collections.EMPTY_SET));
		tests.add(new GiftCertificateCreateDTO(CORRECT_NAME, TOO_LONG_DESC, CORRECT_PRICE, CORRECT_DURATION,
				Collections.EMPTY_SET));
		tests.add(new GiftCertificateCreateDTO(CORRECT_NAME, null, CORRECT_PRICE, CORRECT_DURATION,
				Collections.EMPTY_SET));
		return tests.stream();
	}

	private Stream<GiftCertificateCreateDTO> invalidDurationTestSource() {
		List<GiftCertificateCreateDTO> tests = new ArrayList<>();
		tests.add(new GiftCertificateCreateDTO(CORRECT_NAME, CORRECT_DESC, CORRECT_PRICE, MIN_DURATION - DELTA_DURATION,
				Collections.EMPTY_SET));
		tests.add(new GiftCertificateCreateDTO(CORRECT_NAME, CORRECT_DESC, CORRECT_PRICE, MAX_DURATION + DELTA_DURATION,
				Collections.EMPTY_SET));
		tests.add(new GiftCertificateCreateDTO(CORRECT_NAME, CORRECT_DESC, CORRECT_PRICE, null, Collections.EMPTY_SET));
		return tests.stream();
	}

	private Stream<GiftCertificateCreateDTO> invalidPriceTestSource() {
		List<GiftCertificateCreateDTO> tests = new ArrayList<>();
		tests.add(new GiftCertificateCreateDTO(CORRECT_NAME, CORRECT_DESC, MIN_PRICE - DELTA_PRICE, CORRECT_DURATION,
				Collections.EMPTY_SET));
		tests.add(new GiftCertificateCreateDTO(CORRECT_NAME, CORRECT_DESC, MAX_PRICE + DELTA_PRICE, CORRECT_DURATION,
				Collections.EMPTY_SET));
		tests.add(new GiftCertificateCreateDTO(CORRECT_NAME, CORRECT_DESC, null, CORRECT_DURATION,
				Collections.EMPTY_SET));
		return tests.stream();
	}


	@Test
	public void validateShouldNotThrowExceptionWhenPassedCorrectCertificate() {
		GiftCertificateCreateDTO createDTO =
				new GiftCertificateCreateDTO(CORRECT_NAME, CORRECT_DESC, CORRECT_PRICE, CORRECT_DURATION,
						Collections.EMPTY_SET);
		assertDoesNotThrow(() -> validator.validate(createDTO));
	}

	@ParameterizedTest
	@MethodSource("invalidNameTestSource")
	public void validateShouldThrowExceptionWhenPassedCertificateWithInvalidName(GiftCertificateCreateDTO cert) {
		InvalidCertificateException ex =
				assertThrows(InvalidCertificateException.class, () -> validator.validate(cert));
		assertEquals(InvalidCertificateException.Reason.INVALID_NAME, ex.getReason());
	}

	@ParameterizedTest
	@MethodSource("invalidDescriptionTestSource")
	public void validateShouldThrowExceptionWhenPassedCertificateWithInvalidDescription(GiftCertificateCreateDTO cert) {
		InvalidCertificateException ex =
				assertThrows(InvalidCertificateException.class, () -> validator.validate(cert));
		assertEquals(InvalidCertificateException.Reason.INVALID_DESCRIPTION, ex.getReason());
	}

	@ParameterizedTest
	@MethodSource("invalidDurationTestSource")
	public void validateShouldThrowExceptionWhenPassedCertificateWithInvalidDuration(GiftCertificateCreateDTO cert) {
		InvalidCertificateException ex =
				assertThrows(InvalidCertificateException.class, () -> validator.validate(cert));
		assertEquals(InvalidCertificateException.Reason.INVALID_DURATION, ex.getReason());
	}

	@ParameterizedTest
	@MethodSource("invalidPriceTestSource")
	public void validateShouldThrowExceptionWhenPassedCertificateWithInvalidPrice(GiftCertificateCreateDTO cert) {
		InvalidCertificateException ex =
				assertThrows(InvalidCertificateException.class, () -> validator.validate(cert));
		assertEquals(InvalidCertificateException.Reason.INVALID_PRICE, ex.getReason());
	}

}
