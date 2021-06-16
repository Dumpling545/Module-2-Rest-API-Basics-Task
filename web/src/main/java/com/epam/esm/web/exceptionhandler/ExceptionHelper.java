package com.epam.esm.web.exceptionhandler;

import com.epam.esm.model.dto.Error;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ExceptionHelper {

	@Value("${common.error-info.postfix}")
	private int commonPostfix;

	private int getErrorCode(HttpStatus status, int postfix) {
		return status.value() * 100 + postfix;
	}

	public ResponseEntity<Object> noPostfixHandle(HttpStatus status, String messageTemplate, Object... args) {
		int errorCode = getErrorCode(status, commonPostfix);
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

}
