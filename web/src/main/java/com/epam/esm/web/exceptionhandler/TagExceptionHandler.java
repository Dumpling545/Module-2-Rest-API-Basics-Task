package com.epam.esm.web.exceptionhandler;

import com.epam.esm.model.dto.Error;
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
public class TagExceptionHandler {
	private final String TAG_NOT_FOUND_KEY = "tag.notFound";
	private final String TAG_NOT_FOUND_MSG;
	private final String TAG_ALREADY_EXISTS_KEY = "tag.alreadyExists";
	private final String TAG_ALREADY_EXISTS_MSG;
	private final String TAG_INVALID_NAME_KEY = "tag.invalidName";
	private final String TAG_INVALID_NAME_MSG;
	private final String TAG_POSTFIX_KEY = "tag.postfix";
	private final int TAG_POSTFIX;
	private ExceptionHelper helper;

	@Autowired
	public TagExceptionHandler(ExceptionHelper helper) {
		this.helper = helper;
		ResourceBundle rb = helper.getErrorMessagesBundle();
		TAG_NOT_FOUND_MSG = rb.getString(TAG_NOT_FOUND_KEY);
		TAG_ALREADY_EXISTS_MSG = rb.getString(TAG_ALREADY_EXISTS_KEY);
		TAG_INVALID_NAME_MSG = rb.getString(TAG_INVALID_NAME_KEY);
		TAG_POSTFIX = Integer.parseInt(rb.getString(TAG_POSTFIX_KEY));
	}

	@ExceptionHandler(TagNotFoundException.class)
	public ResponseEntity<Error> handleException(TagNotFoundException ex) {
		return helper.handle(HttpStatus.NOT_FOUND, TAG_NOT_FOUND_MSG, TAG_POSTFIX, ex.getId());
	}

	@ExceptionHandler(InvalidTagNameException.class)
	public ResponseEntity<Error> handleException(InvalidTagNameException ex) {
		return helper
				.handle(HttpStatus.BAD_REQUEST, TAG_INVALID_NAME_MSG, TAG_POSTFIX, ex.getLength(), ex.getMinLength(),
						ex.getMaxLength());
	}

	@ExceptionHandler(TagAlreadyExistsException.class)
	public ResponseEntity<Error> handleException(TagAlreadyExistsException ex) {
		return helper.handle(HttpStatus.CONFLICT, TAG_ALREADY_EXISTS_MSG, TAG_POSTFIX, ex.getTagName());
	}
}
