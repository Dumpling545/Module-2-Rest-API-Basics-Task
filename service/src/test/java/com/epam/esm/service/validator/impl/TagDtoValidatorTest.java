package com.epam.esm.service.validator.impl;

import com.epam.esm.model.dto.TagDTO;
import com.epam.esm.service.exception.InvalidTagException;
import com.epam.esm.service.validator.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(Lifecycle.PER_CLASS)
public class TagDtoValidatorTest {
	private static final int ID = 1;
	private static final int MIN_NAME_LENGTH = 3;
	private static final int MAX_NAME_LENGTH = 30;
	private static final String OUT_OF_BOUNDS_NAME = "sssssssssssssssssssssssssssssssssssssssssssssssssss";
	private static final String NULL_NAME_MESSAGE = "null msg";
	private static final String OUT_OF_BOUNDS_TEMPLATE = "oob msg %d %d %d";
	private static final String MIN_LENGTH_FIELD_NAME = "minNameLength";
	private static final String MAX_LENGTH_FIELD_NAME = "maxNameLength";
	private static final String NULL_NAME_MESSAGE_FIELD_NAME = "nullNameMessage";
	private static final String OUT_OF_BOUNDS_TEMPLATE_FIELD_NAME = "outOfBoundsNameTemplate";
	private static final String CORRECT_NAME = "correct";
	private Validator<TagDTO> tagValidator = new TagValidatorImpl();

	@BeforeAll
	public void init() {
		ReflectionTestUtils.setField(tagValidator, MIN_LENGTH_FIELD_NAME, MIN_NAME_LENGTH);
		ReflectionTestUtils.setField(tagValidator, MAX_LENGTH_FIELD_NAME, MAX_NAME_LENGTH);
		ReflectionTestUtils.setField(tagValidator, NULL_NAME_MESSAGE_FIELD_NAME, NULL_NAME_MESSAGE, String.class);
		ReflectionTestUtils
				.setField(tagValidator, OUT_OF_BOUNDS_TEMPLATE_FIELD_NAME, OUT_OF_BOUNDS_TEMPLATE, String.class);
	}

	@Test
	public void validateShouldThrowExceptionWhenPassedNullNamedTag() {
		TagDTO tagDTO = new TagDTO(ID, null);
		InvalidTagException ex = assertThrows(InvalidTagException.class, () -> tagValidator.validate(tagDTO));
		assertEquals(InvalidTagException.Reason.INVALID_NAME, ex.getReason());
		assertEquals(NULL_NAME_MESSAGE, ex.getMessage());
		assertNull(ex.getTagName());
	}

	@Test
	public void validateShouldThrowExceptionWhenPassedTagWithOutOfBoundsNameLength() {
		TagDTO tagDTO = new TagDTO(ID, OUT_OF_BOUNDS_NAME);
		InvalidTagException ex = assertThrows(InvalidTagException.class, () -> tagValidator.validate(tagDTO));
		assertEquals(InvalidTagException.Reason.INVALID_NAME, ex.getReason());
		String expected =
				String.format(OUT_OF_BOUNDS_TEMPLATE, OUT_OF_BOUNDS_NAME.length(), MIN_NAME_LENGTH, MAX_NAME_LENGTH);
		assertEquals(expected, ex.getMessage());
		assertEquals(OUT_OF_BOUNDS_NAME, ex.getTagName());
	}

	@Test
	public void validateShouldNotThrowExceptionWhenPassedCorrectTag() {
		TagDTO tagDTO = new TagDTO(ID, CORRECT_NAME);
		assertDoesNotThrow(() -> tagValidator.validate(tagDTO));
	}


}
