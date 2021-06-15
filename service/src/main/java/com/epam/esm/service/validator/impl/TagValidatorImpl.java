package com.epam.esm.service.validator.impl;

import com.epam.esm.model.dto.TagDTO;
import com.epam.esm.service.exception.InvalidTagException;
import com.epam.esm.service.validator.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TagValidatorImpl implements Validator<TagDTO> {

	@Value("${tag.name.length.min}")
	private int minNameLength;
	@Value("${tag.name.length.max}")
	private int maxNameLength;
	@Value("${tag.exception.name.null}")
	private String nullNameMessage;
	@Value("${tag.exception.name.out-of-bounds}")
	private String outOfBoundsNameTemplate;


	@Override
	public void validate(TagDTO target) {
		String tagName = target.getName();
		if (target == null) {
			throw new InvalidTagException(nullNameMessage, InvalidTagException.Reason.INVALID_NAME, tagName);
		}
		if (tagName.length() < minNameLength || tagName.length() > maxNameLength) {
			String message = String.format(outOfBoundsNameTemplate, tagName.length(), minNameLength, maxNameLength);
			throw new InvalidTagException(message, InvalidTagException.Reason.INVALID_NAME, tagName);
		}
	}
}
