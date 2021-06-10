package com.epam.esm.service.exception;

import java.io.Serial;

/**
 * Exception that will be thrown if Gift Certificate with provided id does not
 * exist
 */
public class GiftCertificateNotFoundException extends ObjectNotFoundException {
	@Serial
	private static final long serialVersionUID = -8285922327137027020L;

	public GiftCertificateNotFoundException(int id) {
		super(id);
	}
}
