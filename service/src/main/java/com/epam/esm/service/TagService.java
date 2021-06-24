package com.epam.esm.service;

import com.epam.esm.model.dto.TagDTO;

import java.util.List;
import java.util.Set;

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

	/**
	 * Retrieves all tags which names included in provided set from database
	 *
	 * @param tagNames
	 * 		-- {@link Set} of tag names to retrieve
	 * @return {@link List} of tags
	 */
	Set<TagDTO> getTagsFromNameSet(Set<String> tagNames);

	/**
	 * Retrieves all tags associated with provided gift certificate
	 *
	 * @param certificateId
	 * 		id of gift certificate
	 * @return {@link List} with all Tag objects associated with given gift certificate id from database
	 */
	Set<TagDTO> getTagsByCertificate(int certificateId);
}
