package com.epam.esm.service;

import com.epam.esm.model.dto.TagDTO;
import com.epam.esm.service.exception.ServiceException;

import java.util.List;

/**
 * Interface of service for manipulating Tag DTO objects
 */
public interface TagService {
	/**
	 * Creates tag object
	 *
	 * @param tag
	 * 		dto representing tag to be created, id is ignored
	 * @return dto representing created tag
	 */
	TagDTO createTag(TagDTO tag);

	/**
	 * Retrieves tag object by given id
	 *
	 * @param id
	 * 		if of tag to be retrieved
	 * @return tag matching provided id
	 */
	TagDTO getTag(int id);

	/**
	 * Deletes tag object from database
	 *
	 * @param id
	 * 		id of tag to be deleted
	 */
	void deleteTag(int id);

	/**
	 * Retrieves all existing tags from database
	 */
	List<TagDTO> getAllTags();
}
