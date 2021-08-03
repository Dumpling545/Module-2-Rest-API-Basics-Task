package com.epam.esm.web.exceptionhandler;

import com.epam.esm.service.exception.InvalidTagException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Locale;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

/**
 * Exception handler for exceptions associated with tags
 */
@Order(HIGHEST_PRECEDENCE)
@RestControllerAdvice
@RequiredArgsConstructor
public class TagExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(TagExceptionHandler.class);
	private final ExceptionHelper helper;
	private final MessageSource messageSource;
	@Value("${tag.error-info.postfix}")
	private int tagPostfix;

	@ExceptionHandler(InvalidTagException.class)
	public ResponseEntity<Object> handleException(InvalidTagException ex, Locale locale) {
		HttpStatus status;
		String message;
		switch (ex.getReason()) {
			case ALREADY_EXISTS:
				status = HttpStatus.CONFLICT;
				message = messageSource.getMessage("tag.error-message.already-exists",
				                                   new Object[]{ex.getTagDescription()}, locale);
				break;
			case NOT_FOUND:
				status = HttpStatus.NOT_FOUND;
				String tagDescription = ex.getTagDescription();
				message = messageSource.getMessage("tag.error-message.not-found",
				                                   new Object[]{tagDescription}, locale);
				break;
			case INVALID_SORT_BY:
				status = HttpStatus.BAD_REQUEST;
				message = messageSource.getMessage("common.error-message.invalid-sort", null, locale);
				break;
			default:
				status = HttpStatus.INTERNAL_SERVER_ERROR;
				message = messageSource.getMessage("tag.error-message.common", null, locale);
		}
		logger.error("Handled in the InvalidTagException handler", ex);
		return helper.handle(status, message, tagPostfix);
	}
}
