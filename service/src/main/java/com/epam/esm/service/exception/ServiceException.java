package com.epam.esm.service.exception;

import java.io.Serial;

/**
 * Base exception for Service level. All exceptions not covered by its sublasses
 * should be wrapped in Service exception before rethrowing on Web layer.
 */
public class ServiceException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = 6557826832043124416L;

	public ServiceException() {
	}

	public ServiceException(String message) {
		super(message);
	}

	public ServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	public ServiceException(Throwable cause) {
		super(cause);
	}
}
