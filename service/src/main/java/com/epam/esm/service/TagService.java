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
	 * @throws ServiceException
	 */
	void createTag(TagDTO tag) throws ServiceException;

	/**
	 * Retrieves tag object by given id
	 *
	 * @param id
	 * 		if of tag to be retrieved
	 * @return tag matching provided id
	 * @throws ServiceException
	 */
	TagDTO getTag(int id) throws ServiceException;

	/**
	 * Deletes tag object from database
	 *
	 * @param id
	 * 		id of tag to be deleted
	 * @throws ServiceException
	 */
	void deleteTag(int id) throws ServiceException;

	/**
	 * Retrieves all existing tags from database
	 *
	 * @return
	 * @throws ServiceException
	 */
	List<TagDTO> getAllTags() throws ServiceException;
}
