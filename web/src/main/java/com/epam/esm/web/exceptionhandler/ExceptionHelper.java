package com.epam.esm.web.exceptionhandler;

import com.epam.esm.model.dto.Error;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Helper class aimed to reduce boilerplate code while constructing {@link Error} objects
 */
@Component
public class ExceptionHelper {


	private int getErrorCode(HttpStatus status, int postfix) {
		return status.value() * 100 + postfix;
	}

	public ResponseEntity<Object> handleUnformatted(HttpStatus status, List<String> messages, int postfix) {
		return handleUnformatted(status, new HttpHeaders(), messages, postfix);
	}

	public ResponseEntity<Object> handleUnformatted(HttpStatus status, HttpHeaders headers, List<String> messages,
	                                                int postfix) {
		int errorCode = getErrorCode(status, postfix);
		Error error = new Error(errorCode, messages);
		ResponseEntity<Object> responseEntity = ResponseEntity.status(status).headers(headers)
				.contentType(MediaType.APPLICATION_JSON).body(error);
		return responseEntity;
	}

	public ResponseEntity<Object> handle(HttpStatus status, HttpHeaders headers, String messageTemplate, int postfix,
	                                     Object... args) {
		int errorCode = getErrorCode(status, postfix);
		String message = String.format(messageTemplate, args);
		Error error = new Error(errorCode, Collections.singletonList(message));
		ResponseEntity<Object> responseEntity = ResponseEntity.status(status).headers(headers)
				.contentType(MediaType.APPLICATION_JSON).body(error);
		return responseEntity;
	}

	public ResponseEntity<Object> handle(HttpStatus status, String messageTemplate, int postfix, Object... args) {
		return handle(status, new HttpHeaders(), messageTemplate, postfix, args);
	}

}
