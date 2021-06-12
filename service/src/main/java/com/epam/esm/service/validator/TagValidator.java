package com.epam.esm.service.validator;

import com.epam.esm.model.dto.TagDTO;
import com.epam.esm.model.entity.Tag;
import com.epam.esm.service.exception.ServiceException;

public interface TagValidator {
	void validateTag(TagDTO tag) throws ServiceException;

	void validateTagName(String tagName) throws ServiceException;

}
