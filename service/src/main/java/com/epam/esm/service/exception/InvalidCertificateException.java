package com.epam.esm.service.exception;

import lombok.Getter;

/**
 * Exception indicating problems with certificates -- user input does not satisfy constraints or client request is
 * invalid due to current state of specified certificate in database. User friendly specifics about exception can be
 * retrieved using {@link InvalidCertificateException#getReason()}
 */
public class InvalidCertificateException extends ServiceException {
	@Getter
	private final Reason reason;

	@Getter
	private int certificateId;

	public InvalidCertificateException(String message, Reason reason) {
		super(message);
		this.reason = reason;
	}

	public InvalidCertificateException(String message, Reason reason, int certificateId) {
		this(message, reason);
		this.certificateId = certificateId;
	}

	public InvalidCertificateException(String message, Throwable thr, Reason reason) {
		super(message, thr);
		this.reason = reason;
	}

	public InvalidCertificateException(String message, Throwable thr, Reason reason, int certificateId) {
		this(message, thr, reason);
		this.certificateId = certificateId;
	}

	public enum Reason {
		INVALID_NAME, INVALID_DESCRIPTION, INVALID_DURATION, INVALID_PRICE, NOT_FOUND, INVALID_SORT_BY
	}
}
