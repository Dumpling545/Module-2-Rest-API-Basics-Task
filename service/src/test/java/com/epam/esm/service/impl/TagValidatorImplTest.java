package com.epam.esm.service.impl;

import com.epam.esm.model.dto.TagDTO;
import com.epam.esm.service.exception.InvalidStringLengthException;
import com.epam.esm.service.exception.InvalidTagNameException;
import com.epam.esm.service.validator.TagValidator;
import com.epam.esm.service.validator.impl.TagValidatorImpl;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ResourceBundle;
import java.util.stream.Stream;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TagValidatorImplTest {
	TagValidator tagValidator;
	private int minNameLength;
	private int maxNameLength;

	@BeforeAll
	public void initValidator() {
		ResourceBundle rb = ResourceBundle.getBundle("validation");
		tagValidator = new TagValidatorImpl(rb);
		maxNameLength = Integer.parseInt(rb.getString("tag.maxNameLength"));
		minNameLength = Integer.parseInt(rb.getString("tag.minNameLength"));
	}

	private void assertStringParamsEquals(InvalidStringLengthException ex, String name) {
		assertEquals(ex.getLength(), (name == null) ? 0 : name.length());
		assertEquals(ex.getMinLength(), minNameLength);
		assertEquals(ex.getMaxLength(), maxNameLength);
	}


	private Stream<String> validTagNames() {
		int avg = (minNameLength + maxNameLength) / 2;
		return Stream.of(RandomStringUtils.randomAlphabetic(avg), RandomStringUtils.randomAlphabetic(minNameLength),
				RandomStringUtils.randomAlphabetic(maxNameLength));
	}

	@ParameterizedTest
	@MethodSource("validTagNames")
	public void validTagTest(String name) throws Exception {
		TagDTO tag = new TagDTO(name);
		assertDoesNotThrow(() -> tagValidator.validateTag(tag));
	}

	@ParameterizedTest
	@MethodSource("validTagNames")
	public void validTagNameTest(String name) throws Exception {
		assertDoesNotThrow(() -> tagValidator.validateTagName(name));
	}

	private Stream<String> invalidTagNames() {
		return Stream.of(null, "", RandomStringUtils.randomAlphabetic(minNameLength - 1),
				RandomStringUtils.randomAlphabetic(maxNameLength + 1));
	}

	@ParameterizedTest
	@MethodSource("invalidTagNames")
	void invalidTagTest(String name) {
		TagDTO tag = new TagDTO(name);
		InvalidTagNameException ex = assertThrows(InvalidTagNameException.class, () -> tagValidator.validateTag(tag));
		assertStringParamsEquals(ex, name);
	}

	@ParameterizedTest
	@MethodSource("invalidTagNames")
	void invalidTagNameTest(String name) {
		InvalidTagNameException ex =
				assertThrows(InvalidTagNameException.class, () -> tagValidator.validateTagName(name));
		assertStringParamsEquals(ex, name);
	}
}
