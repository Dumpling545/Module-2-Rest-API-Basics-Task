package com.epam.esm.web.exceptionhandler;

import com.epam.esm.model.dto.Error;
import com.epam.esm.service.exception.InvalidCertificateException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class GiftCertificateExceptionHandler {
	private static final Logger logger = LogManager.getLogger();
	@Value("${cert.error-message.not-found}")
	private String certNotFoundMsg;
	@Value("${cert.error-message.invalid-name}")
	private String certInvalidNameMsg;
	@Value("${cert.error-message.invalid-desc}")
	private String certInvalidDescMsg;
	@Value("${cert.error-message.invalid-duration}")
	private String certInvalidDurationMsg;
	@Value("${cert.error-message.invalid-price}")
	private String certInvalidPriceMsg;
	@Value("${cert.error-message.invalid-sort}")
	private String certInvalidSortMsg;
	@Value("${cert.error-info.postfix}")
	private int certPostfix;
	@Value("${common.error-message.service}")
	private String serviceMsg;
	private ExceptionHelper helper;

	public GiftCertificateExceptionHandler(ExceptionHelper helper) {
		this.helper = helper;
	}

	@ExceptionHandler(InvalidCertificateException.class)
	public ResponseEntity<Error> handleException(InvalidCertificateException ex) {
		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
		String message = serviceMsg;
		switch (ex.getReason()) {
			case INVALID_NAME:
				status = HttpStatus.BAD_REQUEST;
				message = certInvalidNameMsg;
				break;
			case INVALID_DESCRIPTION:
				status = HttpStatus.BAD_REQUEST;
				message = certInvalidDescMsg;
				break;
			case INVALID_DURATION:
				status = HttpStatus.BAD_REQUEST;
				message = certInvalidDurationMsg;
				break;
			case INVALID_PRICE:
				status = HttpStatus.BAD_REQUEST;
				message = certInvalidPriceMsg;
				break;
			case NOT_FOUND:
				status = HttpStatus.NOT_FOUND;
				message = String.format(certNotFoundMsg, ex.getCertificateId());
				break;
			case INVALID_SORT_BY:
				status = HttpStatus.BAD_REQUEST;
				message = certInvalidSortMsg;
				break;
		}
		logger.error("", ex);
		return helper.handle(status, message, certPostfix);
	}
}
