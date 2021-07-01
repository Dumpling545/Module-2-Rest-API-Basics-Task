package com.epam.esm.service.converter.impl;

import com.epam.esm.model.dto.GiftCertificateOutputDTO;
import com.epam.esm.model.dto.TagDTO;
import com.epam.esm.model.entity.GiftCertificate;
import com.epam.esm.model.entity.Tag;
import com.epam.esm.service.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class GiftCertificateToGiftCertificateOutputDtoConverter
		implements Converter<GiftCertificate, GiftCertificateOutputDTO> {

	private Converter<Tag, TagDTO> tagConverter;
	public GiftCertificateToGiftCertificateOutputDtoConverter(Converter<Tag, TagDTO> tagConverter){
		this.tagConverter = tagConverter;
	}
	@Override
	public GiftCertificateOutputDTO convert(GiftCertificate giftCertificate) {
		Set<TagDTO> tags = giftCertificate.getTags().stream().map(tagConverter::convert).collect(Collectors.toSet());
		GiftCertificateOutputDTO dto = new GiftCertificateOutputDTO(giftCertificate.getId(), giftCertificate.getName(),
				giftCertificate.getDescription(), giftCertificate.getPrice(), giftCertificate.getDuration(),
				giftCertificate.getCreateDate(), giftCertificate.getLastUpdateDate(), tags);
		return dto;
	}
}
