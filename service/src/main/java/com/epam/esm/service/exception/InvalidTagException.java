package com.epam.esm.service.exception;

import lombok.Getter;

/**
 * Exception indicating problems with tags -- user input does not satisfy constraints or client request is invalid due
 * to current state of specified tag in database. User friendly specifics about exception can be retrieved using {@link
 * InvalidTagException#getReason()}
 */
public class InvalidTagException extends ServiceException {
	@Getter
	private final Reason reason;

	public InvalidTagException(String message, Reason reason) {
		super(message);
		this.reason = reason;
	}

	public InvalidTagException(String message, Throwable thr, InvalidTagException.Reason reason) {
		super(message, thr);
		this.reason = reason;
	}


	public enum Reason {
		INVALID_NAME, NOT_FOUND, ALREADY_EXISTS
	}
}
