package com.epam.esm.service.converter.impl;

import com.epam.esm.model.dto.GiftCertificateOutputDTO;
import com.epam.esm.model.dto.TagDTO;
import com.epam.esm.model.entity.GiftCertificate;
import com.epam.esm.model.entity.Tag;
import com.epam.esm.service.converter.Converter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TagDtoToTagConverterTest {
	private static final int ID = 1;
	private static final String NAME = "name";
	@Test
	public void convertShouldReturnEntityWhenPassedCorrectDto() {
		TagDTO dto = new TagDTO(ID, NAME);
		Converter<TagDTO, Tag> converter = new TagDtoToTagConverter();
		assertDoesNotThrow(() -> {
			Tag entity = converter.convert(dto);
			assertEquals(ID, entity.getId());
			assertEquals(NAME, entity.getName());
		});
	}
}
