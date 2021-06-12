package com.epam.esm.service.merger.impl;

import com.epam.esm.model.dto.GiftCertificateUpdateDTO;
import com.epam.esm.model.entity.GiftCertificate;
import com.epam.esm.service.merger.Merger;

public class GiftCertificateUpdateDtoIntoGiftCertificateMerger
		implements Merger<GiftCertificate, GiftCertificateUpdateDTO> {
	@Override
	public GiftCertificate merge(GiftCertificate base, GiftCertificateUpdateDTO merged) {
		String name = merged.getName() == null ? base.getName() : merged.getName();
		String description = merged.getDescription() == null ? base.getDescription() : merged.getDescription();
		Integer duration = merged.getDuration() == null ? base.getDuration() : merged.getDuration();
		Double price = merged.getPrice() == null ? base.getPrice() : merged.getPrice();
		GiftCertificate certificate = new GiftCertificate(base.getId(), name, description, price, duration, null, null);
		return certificate;
	}
}
