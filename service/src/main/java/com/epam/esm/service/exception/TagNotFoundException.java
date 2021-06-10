package com.epam.esm.service.exception;

import java.io.Serial;

/**
 * Exception that will be thrown if Tag with provided id does not exist
 */
public class TagNotFoundException extends ObjectNotFoundException {
	@Serial
	private static final long serialVersionUID = -8947933628505720455L;

	public TagNotFoundException(int id) {
		super(id);
	}
}
