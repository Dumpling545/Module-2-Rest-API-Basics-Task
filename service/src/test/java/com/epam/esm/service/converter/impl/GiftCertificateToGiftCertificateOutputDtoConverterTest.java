package com.epam.esm.service.converter.impl;

import com.epam.esm.model.dto.GiftCertificateOutputDTO;
import com.epam.esm.model.entity.GiftCertificate;
import com.epam.esm.service.converter.Converter;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GiftCertificateToGiftCertificateOutputDtoConverterTest {
	private static final String NAME = "name";
	private static final String DESCRIPTION = "description";
	private static final int DURATION = 1;
	private static final BigDecimal PRICE = BigDecimal.valueOf(2.3);
	private static final int ID = 1;
	private static final LocalDateTime CREATE_DATE = LocalDateTime.now();
	private static final LocalDateTime LAST_UPDATE_DATE = LocalDateTime.MAX;

	@Test
	public void convertShouldReturnDtoWhenPassedCorrectEntity() {
		GiftCertificate entity = new GiftCertificate(ID, NAME, DESCRIPTION, PRICE, DURATION, CREATE_DATE,
				LAST_UPDATE_DATE, new HashSet<>());
		Converter<GiftCertificate, GiftCertificateOutputDTO> converter =
				new GiftCertificateToGiftCertificateOutputDtoConverter(null);
		assertDoesNotThrow(() -> {
			GiftCertificateOutputDTO dto = converter.convert(entity);
			assertEquals(ID, dto.getId());
			assertEquals(NAME, dto.getName());
			assertEquals(DESCRIPTION, dto.getDescription());
			assertEquals(PRICE, dto.getPrice());
			assertEquals(DURATION, dto.getDuration());
			assertEquals(CREATE_DATE, dto.getCreateDate());
			assertEquals(LAST_UPDATE_DATE, dto.getLastUpdateDate());
			assertNotNull(dto.getTags());
		});
	}
}
