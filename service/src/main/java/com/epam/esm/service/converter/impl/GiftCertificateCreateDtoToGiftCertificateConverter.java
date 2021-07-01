package com.epam.esm.service.converter.impl;

import com.epam.esm.model.dto.GiftCertificateCreateDTO;
import com.epam.esm.model.entity.GiftCertificate;
import com.epam.esm.service.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
public class GiftCertificateCreateDtoToGiftCertificateConverter
		implements Converter<GiftCertificateCreateDTO, GiftCertificate> {

	@Override
	public GiftCertificate convert(GiftCertificateCreateDTO dto) {
		GiftCertificate certificate = new GiftCertificate(null, dto.getName(), dto.getDescription(),
				dto.getPrice(), dto.getDuration(), null, null, new HashSet<>());
		return certificate;
	}
}
