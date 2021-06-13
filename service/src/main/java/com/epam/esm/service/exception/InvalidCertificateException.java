package com.epam.esm.service.exception;

import lombok.Getter;

public class InvalidCertificateException extends ServiceException {
	public enum Reason {
		INVALID_NAME, INVALID_DESCRIPTION, INVALID_DURATION, INVALID_PRICE, NOT_FOUND, INVALID_SORT_BY
	}
	@Getter
	private final Reason reason;
	public InvalidCertificateException(String message, Reason reason){
		super(message);
		this.reason = reason;
	}
}
