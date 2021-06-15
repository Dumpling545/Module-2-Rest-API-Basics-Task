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
	@Getter
	private int tagId;
	@Getter
	private String tagName;

	public InvalidTagException(String message, Reason reason) {
		super(message);
		this.reason = reason;
	}

	public InvalidTagException(String message, Reason reason, int id) {
		this(message, reason);
		this.tagId = id;
	}

	public InvalidTagException(String message, Reason reason, String tagName) {
		this(message, reason);
		this.tagName = tagName;
	}

	public InvalidTagException(String message, Throwable thr, InvalidTagException.Reason reason) {
		super(message, thr);
		this.reason = reason;
	}

	public InvalidTagException(String message, Throwable thr, InvalidTagException.Reason reason, int tagId) {
		this(message, thr, reason);
		this.tagId = tagId;
	}

	public InvalidTagException(String message, Throwable thr, InvalidTagException.Reason reason, String tagName) {
		this(message, thr, reason);
		this.tagName = tagName;
	}

	public enum Reason {
		INVALID_NAME, NOT_FOUND, ALREADY_EXISTS
	}
}
