package com.epam.esm.service.validator.impl;

import com.epam.esm.model.dto.TagDTO;
import com.epam.esm.service.exception.InvalidTagException;
import com.epam.esm.service.validator.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TagValidatorImpl implements Validator<TagDTO> {

	@Value("${tag.validation.name.length.min}")
	private int minNameLength;
	@Value("${tag.validation.name.length.max}")
	private int maxNameLength;
	@Value("${tag.validation.exception.name.null}")
	private String nullNameMessage;
	@Value("${tag.validation.exception.name.out-of-bounds}")
	private String outOfBoundsNameTemplate;


	@Override
	public void validate(TagDTO target) {
		String tagName = target.getName();
		if (tagName == null) {
			throw new InvalidTagException(nullNameMessage, InvalidTagException.Reason.INVALID_NAME, tagName);
		}
		if (tagName.length() < minNameLength || tagName.length() > maxNameLength) {
			String message = String.format(outOfBoundsNameTemplate, tagName.length(), minNameLength, maxNameLength);
			throw new InvalidTagException(message, InvalidTagException.Reason.INVALID_NAME, tagName);
		}
	}
}
