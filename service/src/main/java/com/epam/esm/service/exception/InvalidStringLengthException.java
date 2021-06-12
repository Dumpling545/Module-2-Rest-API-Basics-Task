package com.epam.esm.service.exception;

/**
 * Abstract exception. Its non-abstract subclasses will be thrown if provided String has invalid length in some context
 * (too short or too long). Depending on context, null String also can be treated as String of length 0
 */
public abstract class InvalidStringLengthException extends ServiceException {
	private final int length;
	private final int minLength;
	private final int maxLength;

	public InvalidStringLengthException(int length, int minLength, int maxLength) {
		this.length = length;
		this.minLength = minLength;
		this.maxLength = maxLength;
	}

	public int getLength() {
		return length;
	}

	public int getMinLength() {
		return minLength;
	}

	public int getMaxLength() {
		return maxLength;
	}
}
