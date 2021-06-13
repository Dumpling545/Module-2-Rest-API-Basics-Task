package com.epam.esm.service.exception;

import lombok.Getter;

public class InvalidTagException extends ServiceException{
	public enum Reason {
		INVALID_NAME, NOT_FOUND, ALREADY_EXISTS
	}
	@Getter
	private final Reason reason;

	public InvalidTagException(String message, Reason reason){
		super(message);
		this.reason = reason;
	}
}
