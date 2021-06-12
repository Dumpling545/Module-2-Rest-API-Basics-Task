package com.epam.esm.web.helper;

import com.epam.esm.model.dto.Error;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ResourceBundle;

public class ExceptionHelper {

	private ResourceBundle errorMessagesBundle;

	public ExceptionHelper(ResourceBundle errorMessagesBundle) {
		this.errorMessagesBundle = errorMessagesBundle;
	}

	private int getErrorCode(HttpStatus status, int postfix) {
		return status.value() * 100 + postfix;
	}

	public ResponseEntity<Object> noPostfixHandle(HttpStatus status, String messageTemplate, Object... args) {
		int errorCode = getErrorCode(status, 0);
		String message = String.format(messageTemplate, args);
		Error error = new Error(errorCode, message);
		return new ResponseEntity<>(error, status);
	}

	public ResponseEntity<Error> handle(HttpStatus status, String messageTemplate, int postfix, Object... args) {
		int errorCode = getErrorCode(status, postfix);
		String message = String.format(messageTemplate, args);
		Error error = new Error(errorCode, message);
		return new ResponseEntity<>(error, status);
	}

	public ResourceBundle getErrorMessagesBundle() {
		return errorMessagesBundle;
	}
}
