package com.epam.esm.service;

import com.epam.esm.model.dto.PageDTO;
import com.epam.esm.model.dto.PagedResultDTO;
import com.epam.esm.model.dto.TagDTO;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

/**
 * Interface of service for manipulating Tag DTO objects
 */
@Validated
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
	 * Retrieves tag object by given name
	 *
	 * @param tagName
	 * 		if of tag to be retrieved
	 * @return tag matching provided name
	 */
	TagDTO getTag(String tagName);

	/**
	 * Deletes tag object from database
	 *
	 * @param id
	 * 		id of tag to be deleted
	 */
	void deleteTag(int id);

	/** Retrieves all existing tags from database
	 * @param page paging info
	 * @return paged list of tags
	 */
	PagedResultDTO<TagDTO> getAllTags(@Valid PageDTO page);

	/**
	 * Retrieves all tags which names included in provided set from database
	 *
	 * @param tagNames
	 * 		-- {@link Set} of tag names to retrieve
	 * @return {@link List} of tags
	 */
	Set<TagDTO> getTagsFromNameSet(Set<String> tagNames);

	/**
	 * Retrieves tag with maximal number of associations among all certificates purchased by specified user. If there
	 * are more than 1 user matching, tag with maximal aggregate cost of all orders is chosen.
	 * <br>
	 * NOTE: Associations with certificates counted on per-order basis (1 order - 1 association for each tag associated
	 * with purchased certificate).
	 *
	 * @param userId
	 * 		-- id of user
	 * @return  corresponding tag object
	 */
	TagDTO getMostWidelyUsedTagOfUserWithHighestCostOfAllOrders(int userId);
}
