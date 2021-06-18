package com.epam.esm.web.exceptionhandler;

import com.epam.esm.model.dto.Error;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ExceptionHelper {


	private int getErrorCode(HttpStatus status, int postfix) {
		return status.value() * 100 + postfix;
	}

	public ResponseEntity<Object> handle(HttpStatus status, HttpHeaders headers, String messageTemplate, int postfix,
	                                     Object... args) {
		int errorCode = getErrorCode(status, postfix);
		String message = String.format(messageTemplate, args);
		Error error = new Error(errorCode, message);
		ResponseEntity<Object> responseEntity = ResponseEntity.status(status)
				.contentType(MediaType.APPLICATION_JSON).body(error);
		return responseEntity;
	}

	public ResponseEntity<Object> handle(HttpStatus status, String messageTemplate, int postfix, Object... args) {
		return handle(status, new HttpHeaders(), messageTemplate, postfix, args);
	}

}
