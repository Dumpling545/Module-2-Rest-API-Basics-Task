package com.epam.esm.db;


import com.epam.esm.model.entity.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Interface for managing Tag objects in database
 */
public interface TagRepository extends Repository<Tag, Integer> {
	/**
	 * Creates new tag in database (id property is ignored).
	 *
	 * @param tag object containing name for new tag, id field is ignored
	 * @return Tag object representing newly created tag in database
	 */
	Tag save(Tag tag);

	/**
	 * Saves all given tags.
	 *
	 * @param entities must not be {@literal null} nor must it contain {@literal null}.
	 * @return the saved entities; will never be {@literal null}. The returned {@literal Iterable} will have the same size
	 * as the {@literal Iterable} passed as an argument.
	 * @throws IllegalArgumentException in case the given {@link Iterable entities} or one of its entities is
	 *                                  {@literal null}.
	 */
	<S extends Tag> Set<S> saveAll(Iterable<S> entities);

	/**
	 * Retrieves tag with given id
	 *
	 * @param id id of tag to be retrieved
	 * @return {@link Optional} containing corresponding tag object, if tag with such id exists in database; empty
	 * {@link Optional} otherwise
	 */
	Optional<Tag> findById(Integer id);

	/**
	 * Returns whether tag with the given id exists.
	 *
	 * @param integer must not be {@literal null}.
	 * @return {@literal true} if an entity with the given id exists, {@literal false} otherwise.
	 * @throws IllegalArgumentException if {@literal id} is {@literal null}.
	 */
	boolean existsById(Integer integer);


	/**
	 * Retrieves tag with given name
	 *
	 * @param tagName name of tag to be retrieved
	 * @return {@link Optional} of tag containing corresponding tag object, if tag with such name exists in database;
	 * empty {@link Optional} otherwise
	 */
	Optional<Tag> getTagByName(String tagName);

	/**
	 * Retrieves all tags in database
	 *
	 * @param pageable paging info
	 * @return paged list of tags
	 */
	Slice<Tag> getAllTagsBy(Pageable pageable);

	/**
	 * Retrieves all tags which names included in provided set from database
	 *
	 * @param tagNames -- {@link Set} of tag names to retrieve
	 * @return {@link List} of tags
	 */
	List<Tag> getTagsByNameIn(Set<String> tagNames);

	/**
	 * Deletes tag with given id from database
	 *
	 * @param id id of tag to be deleted
	 * @return true if tag with given id successfully deleted; false if tag with such id does not exist in database by
	 * the time of method invocation
	 */
	void deleteById(Integer id);

	/**
	 * Retrieves tag with maximal number of associations among all certificates purchased by specified user. If there
	 * are more than 1 user matching, tag with maximal aggregate cost of all orders is chosen.
	 * <br>
	 * NOTE: Associations with certificates counted on per-order basis (1 order - 1 association for each tag associated
	 * with purchased certificate).
	 *
	 * @param userId -- id of user
	 * @return {@link Optional} containing corresponding tag object, if tag with such id exists in database; empty
	 * {@link Optional} otherwise
	 */
	@Query(value = "SELECT tag.* FROM tag " +
	               "INNER JOIN tag_gift_certificate ON tag_gift_certificate.tag_id=tag.id " +
	               "INNER JOIN cert_order ON cert_order.gift_certificate_id=tag_gift_certificate.gift_certificate_id " +
	               "WHERE user_id=:userId GROUP BY tag.id, tag.name " +
	               "ORDER BY COUNT(cert_order.id) DESC, SUM(cert_order.cost) DESC LIMIT 1", nativeQuery = true)
	Optional<Tag> getMostWidelyUsedTagOfUserWithHighestCostOfAllOrders(@Param("userId") int userId);

}
