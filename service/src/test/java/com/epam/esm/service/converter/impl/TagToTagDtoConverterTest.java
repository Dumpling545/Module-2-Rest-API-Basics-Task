package com.epam.esm.service.converter.impl;

import com.epam.esm.model.dto.TagDTO;
import com.epam.esm.model.entity.Tag;
import com.epam.esm.service.converter.Converter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TagToTagDtoConverterTest {
	private static final int ID = 1;
	private static final String NAME = "name";

	@Test
	public void convertShouldReturnDtoWhenPassedCorrectEntity() {
		Tag entity = new Tag(ID, NAME);
		Converter<Tag, TagDTO> converter = new TagToTagDtoConverter();
		assertDoesNotThrow(() -> {
			TagDTO dto = converter.convert(entity);
			assertEquals(ID, dto.getId());
			assertEquals(NAME, dto.getName());
		});
	}
}
