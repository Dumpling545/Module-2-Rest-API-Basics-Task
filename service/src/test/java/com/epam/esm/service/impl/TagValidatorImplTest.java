package com.epam.esm.service.impl;

import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TagValidatorImplTest {
	/*
	TagValidator tagValidator;
	private int minNameLength;
	private int maxNameLength;

	@BeforeAll
	public void initValidator() {
		ResourceBundle rb = ResourceBundle.getBundle("validation");
		tagValidator = new TagValidatorImpl(rb);
		maxNameLength = Integer.parseInt(rb.getString("tag.name.length.max"));
		minNameLength = Integer.parseInt(rb.getString("tag.name.length.min"));
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
	}*/
}
