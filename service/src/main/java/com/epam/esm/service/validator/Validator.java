package com.epam.esm.service.validator;

public interface Validator<T> {
	void validate(T target);
}
