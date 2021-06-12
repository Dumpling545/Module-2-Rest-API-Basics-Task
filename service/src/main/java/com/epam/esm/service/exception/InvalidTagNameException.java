package com.epam.esm.service.exception;

import java.io.Serial;


/**
 * Exception that will be thrown if provided Tag has invalid name length (too short or too long). Depending on context,
 * null name also can be treated as name of length 0
 */
public class InvalidTagNameException extends InvalidStringLengthException {
	@Serial
	private static final long serialVersionUID = 442027800265889457L;

	public InvalidTagNameException(int length, int minLength, int maxLength) {
		super(length, minLength, maxLength);
	}
}
