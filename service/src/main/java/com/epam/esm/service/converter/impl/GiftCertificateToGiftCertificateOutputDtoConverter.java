package com.epam.esm.service.converter.impl;

import com.epam.esm.model.dto.GiftCertificateOutputDTO;
import com.epam.esm.model.entity.GiftCertificate;
import com.epam.esm.service.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
public class GiftCertificateToGiftCertificateOutputDtoConverter implements Converter<GiftCertificate, GiftCertificateOutputDTO> {

	@Override
	public GiftCertificateOutputDTO convert(GiftCertificate giftCertificate) {
		GiftCertificateOutputDTO dto = new GiftCertificateOutputDTO(giftCertificate.getId(), giftCertificate.getName(),
				giftCertificate.getDescription(), giftCertificate.getPrice(), giftCertificate.getDuration(),
				giftCertificate.getCreateDate(), giftCertificate.getLastUpdateDate(), new HashSet<>());
		return dto;
	}
}
