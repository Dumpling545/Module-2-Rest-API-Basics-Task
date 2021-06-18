package com.epam.esm.service.merger.impl;

import com.epam.esm.model.dto.GiftCertificateUpdateDTO;
import com.epam.esm.model.entity.GiftCertificate;
import com.epam.esm.service.merger.Merger;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class GiftCertificateUpdateDtoIntoGiftCertificateMerger
		implements Merger<GiftCertificate, GiftCertificateUpdateDTO> {
	@Override
	public GiftCertificate merge(GiftCertificate base, GiftCertificateUpdateDTO merged) {
		String name = merged.getName() == null ? base.getName() : merged.getName();
		String description = merged.getDescription() == null ? base.getDescription() : merged.getDescription();
		Integer duration = merged.getDuration() == null ? base.getDuration() : merged.getDuration();
		BigDecimal price = merged.getPrice() == null ? base.getPrice() : merged.getPrice();
		GiftCertificate certificate = new GiftCertificate(base.getId(), name, description, price, duration,
				base.getCreateDate(), base.getLastUpdateDate());
		return certificate;
	}
}
