package com.epam.esm.service.converter.impl;

import com.epam.esm.model.dto.GiftCertificateCreateDTO;
import com.epam.esm.model.entity.GiftCertificate;
import com.epam.esm.service.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class GiftCertificateCreateDtoToGiftCertificateConverter
		implements Converter<GiftCertificateCreateDTO, GiftCertificate> {
	private final static int DEFAULT_ID = -1;

	@Override
	public GiftCertificate convert(GiftCertificateCreateDTO dto) {
		GiftCertificate certificate = new GiftCertificate(DEFAULT_ID, dto.getName(), dto.getDescription(),
				dto.getPrice(), dto.getDuration(), null, null);
		return certificate;
	}
}
