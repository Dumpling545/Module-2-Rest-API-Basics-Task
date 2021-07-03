package com.epam.esm.web.exceptionhandler;

import com.epam.esm.service.exception.InvalidTagException;
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

@Order(HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class TagExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(TagExceptionHandler.class);
	@Value("${tag.error-info.postfix}")
	private int tagPostfix;
	private ExceptionHelper helper;
	private MessageSource messageSource;

	public TagExceptionHandler(ExceptionHelper helper, MessageSource messageSource) {
		this.helper = helper;
		this.messageSource = messageSource;
	}

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
			default:
				status = HttpStatus.INTERNAL_SERVER_ERROR;
				message = messageSource.getMessage("tag.error-message.common", null, locale);
		}
		logger.error("Handled in the InvalidTagException handler", ex);
		return helper.handle(status, message, tagPostfix);
	}
}
