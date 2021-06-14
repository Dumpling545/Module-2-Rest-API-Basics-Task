package com.epam.esm.web.exceptionhandler;

import com.epam.esm.model.dto.Error;
import com.epam.esm.service.exception.InvalidTagException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class TagExceptionHandler {

	@Value("${tag.error-message.not-found}")
	private String tagNotFoundMsg;
	@Value("${tag.error-message.already-exists}")
	private String tagAlreadyExistsMsg;
	@Value("${tag.error-message.invalid-name}")
	private String tagInvalidNameMsg;
	@Value("${tag.error-info.postfix}")
	private int tagPostfix;
	@Value("${common.error-message.service}")
	private String serviceMsg;

	private ExceptionHelper helper;

	@Autowired
	public TagExceptionHandler(ExceptionHelper helper) {
		this.helper = helper;
	}

	@ExceptionHandler(InvalidTagException.class)
	public ResponseEntity<Error> handleException(InvalidTagException ex) {
		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
		String message = serviceMsg;
		switch (ex.getReason()){
			case INVALID_NAME:
				status = HttpStatus.BAD_REQUEST;
				message = tagInvalidNameMsg;
				break;
			case ALREADY_EXISTS:
				status = HttpStatus.CONFLICT;
				message = tagAlreadyExistsMsg;
				break;
			case NOT_FOUND:
				status = HttpStatus.NOT_FOUND;
				message = tagNotFoundMsg;
				break;
		}
		return helper.handle(status, message, tagPostfix);
	}
}
