package com.epam.esm.service.converter.impl;

import com.epam.esm.model.dto.GiftCertificateOutputDTO;
import com.epam.esm.model.entity.GiftCertificate;
import com.epam.esm.service.converter.Converter;

import java.util.HashSet;

public class GiftCertificateToGiftCertificateOutputDto implements Converter<GiftCertificate, GiftCertificateOutputDTO> {

	@Override
	public GiftCertificateOutputDTO convert(GiftCertificate giftCertificate) {
		GiftCertificateOutputDTO dto = new GiftCertificateOutputDTO(giftCertificate.getId(), giftCertificate.getName(),
				giftCertificate.getDescription(), giftCertificate.getPrice(), giftCertificate.getDuration(),
				giftCertificate.getCreateDate(), giftCertificate.getLastUpdateDate(), new HashSet<>());
		return dto;
	}
}
