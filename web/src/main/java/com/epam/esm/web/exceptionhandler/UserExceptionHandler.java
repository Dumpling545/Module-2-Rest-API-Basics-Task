package com.epam.esm.web.exceptionhandler;

import com.epam.esm.service.exception.InvalidOrderException;
import com.epam.esm.service.exception.InvalidUserException;
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
public class UserExceptionHandler {
	private static final Logger logger = LoggerFactory.getLogger(UserExceptionHandler.class);
	@Value("${user.error-info.postfix}")
	private int userPostfix;
	private ExceptionHelper helper;
	private MessageSource messageSource;

	public UserExceptionHandler(ExceptionHelper helper, MessageSource messageSource) {
		this.helper = helper;
		this.messageSource = messageSource;
	}

	@ExceptionHandler(InvalidUserException.class)
	public ResponseEntity<Object> handleException(InvalidUserException ex, Locale locale) {
		HttpStatus status;
		String message;
		switch (ex.getReason()) {
			case NOT_FOUND:
				status = HttpStatus.NOT_FOUND;
				String identifier = "id=" + ex.getUserId();
				message = messageSource.getMessage("user.error-message.not-found",
						new Object[]{identifier}, locale);
				break;
			default:
				status = HttpStatus.INTERNAL_SERVER_ERROR;
				message = messageSource.getMessage("user.error-message.common", null, locale);
		}
		logger.error("Handled in the InvalidUserException handler", ex);
		return helper.handle(status, message, userPostfix);
	}
}