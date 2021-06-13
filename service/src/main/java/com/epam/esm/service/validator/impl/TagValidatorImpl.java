package com.epam.esm.service.validator.impl;

import com.epam.esm.model.dto.TagDTO;
import com.epam.esm.model.entity.Tag;
import com.epam.esm.service.exception.InvalidTagNameException;
import com.epam.esm.service.exception.ServiceException;
import com.epam.esm.service.validator.TagValidator;
import com.epam.esm.service.validator.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ResourceBundle;
@Component
public class TagValidatorImpl implements Validator<TagDTO> {

	@Value("${tag.minNameLength}")
	private int minNameLength;
	@Value("${tag.maxNameLength}")
	private int maxNameLength;


	@Override
	public void validate(TagDTO target) {
		String tagName = target.getName();
		if(target == null){
			throw new InvalidTagNameException(0, minNameLength, maxNameLength);
		}
		if (tagName.length() < minNameLength || tagName.length() > maxNameLength) {
			throw new InvalidTagNameException(tagName.length(), minNameLength, maxNameLength);
		}
	}
}
