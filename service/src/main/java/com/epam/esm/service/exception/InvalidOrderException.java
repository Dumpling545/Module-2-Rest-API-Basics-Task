package com.epam.esm.service.exception;

import lombok.Getter;

public class InvalidOrderException extends ServiceException{
	@Getter
	private Integer id;
	@Getter
	private final Reason reason;
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
