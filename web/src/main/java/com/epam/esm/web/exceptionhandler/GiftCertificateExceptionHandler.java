package com.epam.esm.web.exceptionhandler;

import com.epam.esm.model.dto.Error;
import com.epam.esm.web.helper.ExceptionHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ResourceBundle;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class GiftCertificateExceptionHandler {
	private final String CERT_NOT_FOUND_KEY = "cert.notFound";
	private final String CERT_NOT_FOUND_MSG;
	private final String CERT_INVALID_NAME_KEY = "cert.invalidName";
	private final String CERT_INVALID_NAME_MSG;
	private final String CERT_INVALID_DESC_KEY = "cert.invalidDesc";
	private final String CERT_INVALID_DESC_MSG;
	private final String CERT_INVALID_DURATION_KEY = "cert.invalidDuration";
	private final String CERT_INVALID_DURATION_MSG;
	private final String CERT_INVALID_PRICE_KEY = "cert.invalidPrice";
	private final String CERT_INVALID_PRICE_MSG;
	private final String CERT_POSTFIX_KEY = "cert.postfix";
	private final int CERT_POSTFIX;

	private ExceptionHelper helper;

	@Autowired
	public GiftCertificateExceptionHandler(ExceptionHelper helper) {
		this.helper = helper;
		ResourceBundle rb = helper.getErrorMessagesBundle();
		CERT_NOT_FOUND_MSG = rb.getString(CERT_NOT_FOUND_KEY);
		CERT_INVALID_NAME_MSG = rb.getString(CERT_INVALID_NAME_KEY);
		CERT_INVALID_DESC_MSG = rb.getString(CERT_INVALID_DESC_KEY);
		CERT_INVALID_DURATION_MSG = rb.getString(CERT_INVALID_DURATION_KEY);
		CERT_INVALID_PRICE_MSG = rb.getString(CERT_INVALID_PRICE_KEY);
		CERT_POSTFIX = Integer.parseInt(rb.getString(CERT_POSTFIX_KEY));
	}

	@ExceptionHandler(GiftCertificateNotFoundException.class)
	public ResponseEntity<Error> handleException(GiftCertificateNotFoundException ex) {
		return helper.handle(HttpStatus.NOT_FOUND, CERT_NOT_FOUND_MSG, CERT_POSTFIX, ex.getId());
	}

	@ExceptionHandler(InvalidCertificateNameException.class)
	public ResponseEntity<Error> handleException(InvalidCertificateNameException ex) {
		return helper
				.handle(HttpStatus.BAD_REQUEST, CERT_INVALID_NAME_MSG, CERT_POSTFIX, ex.getLength(), ex.getMinLength(),
						ex.getMaxLength());
	}

	@ExceptionHandler(InvalidCertificateDescriptionException.class)
	public ResponseEntity<Error> handleException(InvalidCertificateDescriptionException ex) {
		return helper
				.handle(HttpStatus.BAD_REQUEST, CERT_INVALID_DESC_MSG, CERT_POSTFIX, ex.getLength(), ex.getMinLength(),
						ex.getMaxLength());
	}

	@ExceptionHandler(InvalidCertificateDurationException.class)
	public ResponseEntity<Error> handleException(InvalidCertificateDurationException ex) {
		return helper.handle(HttpStatus.BAD_REQUEST, CERT_INVALID_DURATION_MSG, CERT_POSTFIX, ex.getDuration(),
				ex.getMinDuration(), ex.getMaxDuration());
	}

	@ExceptionHandler(InvalidCertificatePriceException.class)
	public ResponseEntity<Error> handleException(InvalidCertificatePriceException ex) {
		return helper
				.handle(HttpStatus.BAD_REQUEST, CERT_INVALID_PRICE_MSG, CERT_POSTFIX, ex.getPrice(), ex.getMinPrice(),
						ex.getMaxPrice());
	}
}
