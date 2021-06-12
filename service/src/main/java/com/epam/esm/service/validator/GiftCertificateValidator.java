package com.epam.esm.service.validator;

import com.epam.esm.model.dto.GiftCertificateDTO;

public interface GiftCertificateValidator {
	void validateCertificate(GiftCertificateDTO certificate, boolean ignoreMissing);
}
