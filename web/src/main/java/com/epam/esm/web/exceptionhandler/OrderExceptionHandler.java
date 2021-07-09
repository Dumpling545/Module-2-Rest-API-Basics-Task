package com.epam.esm.web.exceptionhandler;

import com.epam.esm.service.exception.InvalidOrderException;
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
 * Exception handler for exceptions associated with orders
 */
@Order(HIGHEST_PRECEDENCE)
@RestControllerAdvice
@RequiredArgsConstructor
public class OrderExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(OrderExceptionHandler.class);
	@Value("${order.error-info.postfix}")
	private int orderPostfix;
	private final ExceptionHelper helper;
	private final MessageSource messageSource;

	@ExceptionHandler(InvalidOrderException.class)
	public ResponseEntity<Object> handleException(InvalidOrderException ex, Locale locale) {
		HttpStatus status;
		String message;
		switch (ex.getReason()) {
			case NOT_FOUND:
				status = HttpStatus.NOT_FOUND;
				String identifier = "id=" + ex.getId();
				message = messageSource.getMessage("order.error-message.not-found",
						new Object[]{identifier}, locale);
				break;
			default:
				status = HttpStatus.INTERNAL_SERVER_ERROR;
				message = messageSource.getMessage("order.error-message.common", null, locale);
		}
		logger.error("Handled in the InvalidOrderException handler", ex);
		return helper.handle(status, message, orderPostfix);
	}
}

