package com.epam.esm.service.converter.impl;

import com.epam.esm.model.dto.GiftCertificateCreateDTO;
import com.epam.esm.model.entity.GiftCertificate;
import com.epam.esm.service.converter.Converter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GiftCertificateCreateDtoToGiftCertificateConverterTest {
	private static final String NAME = "name";
	private static final String DESCRIPTION = "description";
	private static final int DURATION = 1;
	private static final double PRICE = 2.3;

	@Test
	public void convertShouldReturnEntityWhenPassedCorrectDto() {
		GiftCertificateCreateDTO dto = new GiftCertificateCreateDTO(NAME, DESCRIPTION, PRICE, DURATION, null);
		Converter<GiftCertificateCreateDTO, GiftCertificate> converter =
				new GiftCertificateCreateDtoToGiftCertificateConverter();
		assertDoesNotThrow(() -> {
			GiftCertificate cert = converter.convert(dto);
			assertEquals(NAME, cert.getName());
			assertEquals(DESCRIPTION, cert.getDescription());
			assertEquals(PRICE, cert.getPrice());
			assertEquals(DURATION, cert.getDuration());
		});
	}
}
