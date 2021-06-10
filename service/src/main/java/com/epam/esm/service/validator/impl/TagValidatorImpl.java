package com.epam.esm.service.validator.impl;

import com.epam.esm.model.dto.TagDTO;
import com.epam.esm.model.entity.Tag;
import com.epam.esm.service.exception.InvalidTagNameException;
import com.epam.esm.service.exception.ServiceException;
import com.epam.esm.service.validator.TagValidator;

import java.util.ResourceBundle;

public class TagValidatorImpl implements TagValidator {

	private final String MIN_NAME_LENGTH_KEY = "tag.minNameLength";
	private final int MIN_NAME_LENGTH;
	private final String MAX_NAME_LENGTH_KEY = "tag.maxNameLength";
	private final int MAX_NAME_LENGTH;

	public TagValidatorImpl(ResourceBundle bounds) {
		MIN_NAME_LENGTH =
				Integer.parseInt(bounds.getString(MIN_NAME_LENGTH_KEY));
		MAX_NAME_LENGTH =
				Integer.parseInt(bounds.getString(MAX_NAME_LENGTH_KEY));
	}

	@Override
	public void validateTag(TagDTO tag)
			throws ServiceException
	{
		validateTagName(tag.getName());
	}

	@Override
	public void validateTagName(String tagName)
			throws ServiceException
	{
		if (MIN_NAME_LENGTH > 0 && (tagName == null || tagName.isBlank())) {
			throw new InvalidTagNameException(0, MIN_NAME_LENGTH,
					MAX_NAME_LENGTH);
		}
		if (tagName.length() < MIN_NAME_LENGTH ||
				tagName.length() > MAX_NAME_LENGTH)
		{
			throw new InvalidTagNameException(tagName.length(),
					MIN_NAME_LENGTH,
					MAX_NAME_LENGTH);
		}
	}
}
