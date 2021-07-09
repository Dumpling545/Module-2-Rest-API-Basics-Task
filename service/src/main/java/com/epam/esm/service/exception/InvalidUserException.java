package com.epam.esm.service.exception;

import lombok.Getter;

/**
 * Exception indicating problems with user objects -- user input does not satisfy constraints or client request is invalid due
 * to current state of specified user object in database. User friendly specifics about exception can be retrieved using {@link
 * InvalidUserException#getReason()}
 */
public class InvalidUserException extends ServiceException {
	@Getter
	private final Reason reason;
	@Getter
	private Integer userId;
	@Getter
	private String userName;

	public InvalidUserException(String message, Reason reason) {
		super(message);
		this.reason = reason;
	}

	public InvalidUserException(String message, Reason reason, int userId) {
		this(message, reason);
		this.userId = userId;
	}
	public InvalidUserException(String message, Throwable thr, Reason reason, int userId) {
		super(message, thr);
		this.reason = reason;
		this.userId = userId;
	}

	public enum Reason {
		NOT_FOUND
	}
}
