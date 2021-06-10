package com.epam.esm.service.exception;

import java.io.Serial;

/**
 * Abstract exception. Its non-abstract subclasses will be thrown if object with
 * provided id does not exists
 */
public abstract class ObjectNotFoundException extends ServiceException {
	@Serial
	private static final long serialVersionUID = 3980971948415903405L;
	private final int id;

	public ObjectNotFoundException(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
}
