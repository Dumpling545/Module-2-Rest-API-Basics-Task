package com.epam.esm.service.exception;

import java.io.Serial;

/**
 * Exception that will be thrown if during tag creation process if tag with provided name already exists in database
 */
public class TagAlreadyExistsException extends ServiceException {
	@Serial
	private static final long serialVersionUID = 5051254073035271730L;
	private final String tagName;

	public TagAlreadyExistsException(Throwable cause, String tagName) {
		super(cause);
		this.tagName = tagName;
	}


	public String getTagName() {
		return tagName;
	}
}
