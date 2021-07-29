package com.epam.esm.service.exception;

import lombok.Getter;

/**
 * Exception indicating problems with orders -- user input does not satisfy constraints or client request is
 * invalid due to current state of specified order in database. User friendly specifics about exception can be
 * retrieved using {@link InvalidOrderException#getReason()}
 */
public class InvalidOrderException extends ServiceException {
	@Getter
	private final Reason reason;
	@Getter
	private Integer id;

	public InvalidOrderException(String message, Reason reason) {
		super(message);
		this.reason = reason;
	}

	public InvalidOrderException(String message, Reason reason, int id) {
		this(message, reason);
		this.id = id;
	}

	public enum Reason {
		NOT_FOUND
	}
}
