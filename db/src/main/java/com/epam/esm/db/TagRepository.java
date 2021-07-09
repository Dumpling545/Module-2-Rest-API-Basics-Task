package com.epam.esm.db;


import com.epam.esm.model.entity.PagedResult;
import com.epam.esm.model.entity.Tag;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Interface for managing Tag objects in database
 */
public interface TagRepository {
	/**
	 * Creates new tag in database (id property is ignored).
	 *
	 * @param tag
	 * 		object containing name for new tag, id field is ignored
	 * @return Tag object representing newly created tag in database
	 */
	Tag createTag(Tag tag);

	/**
	 * Retrieves tag with given id
	 *
	 * @param id
	 * 		id of tag to be retrieved
	 * @return {@link Optional} of tag containing corresponding tag object, if tag with such id exists in database;
	 * empty {@link Optional} otherwise
	 */
	Optional<Tag> getTagById(int id);

	/**
	 * Retrieves tag with given name
	 *
	 * @param tagName
	 * 		name of tag to be retrieved
	 * @return {@link Optional} of tag containing corresponding tag object, if tag with such name exists in database;
	 * empty {@link Optional} otherwise
	 */
	Optional<Tag> getTagByName(String tagName);

	/**
	 * Retrieves all tags in database
	 *
	 * @return {@link List} with all Tag objects from database
	 */
	PagedResult<Tag> getAllTags(int offset, int limit);
	/**
	 * Retrieves all tags which names included in provided set from database
	 *
	 * @param tagNames
	 * 		-- {@link Set} of tag names to retrieve
	 * @return {@link List} of tags
	 */
	List<Tag> getTagsFromNameSet(Set<String> tagNames);

	/**
	 * Deletes tag with given id from database
	 *
	 * @param id
	 * 		id of tag to be deleted
	 * @return true if tag with given id successfully deleted; false if tag with such id does not exist in database by
	 * the time of method invocation
	 */
	boolean deleteTag(int id);

	Optional<Tag> getMostWidelyUsedTagOfUserWithHighestCostOfAllOrders(int userId);

}
