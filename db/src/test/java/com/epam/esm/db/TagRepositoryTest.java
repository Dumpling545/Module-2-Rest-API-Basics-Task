package com.epam.esm.db;

import com.epam.esm.GiftCertificateSystemApplication;
import com.epam.esm.model.entity.PagedResult;
import com.epam.esm.model.entity.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith({SpringExtension.class})
@SpringBootTest(classes = GiftCertificateSystemApplication.class)
@AutoConfigureTestDatabase
public class TagRepositoryTest {
	private static final Tag existingTag1 = new Tag(1, "tag1");
	private static final Tag existingTag2 = new Tag(2, "tag2");
	private static final Tag existingTag3 = new Tag(3, "tag3");
	private static final Tag existingTagWithoutId = new Tag(null, "tag4");
	private static final Tag existingTag8 = new Tag(8, "tag8");
	private static final Tag nonExistingTag = new Tag(-1, "tag0");
	private static final Tag nonExistingTagToBeCreated = new Tag(null, "tag1900");
	private static final int ALL_TAGS_EXISTING_OFFSET = 0;
	private static final int ALL_TAGS_EXISTING_LIMIT = 5;
	private static final int ALL_TAGS_NON_EXISTING_OFFSET = 100;
	private static final int ALL_TAGS_NON_EXISTING_LIMIT = 5;
	private static final int EXISTING_USER_ID = 1;
	@Autowired
	TagRepository tagRepository;

	@Test
	public void getAllTagsShouldReturnNonEmptyListWhenPassedExistingOffsetAndLimit() {
		PagedResult<Tag> tags = tagRepository.getAllTags(ALL_TAGS_EXISTING_OFFSET, ALL_TAGS_EXISTING_LIMIT);
		assertFalse(tags.getPage().isEmpty());
	}

	@Test
	public void getAllTagsShouldReturnEmptyListWhenPassedNonExistingOffsetAndLimit() {
		PagedResult<Tag> tags = tagRepository.getAllTags(ALL_TAGS_NON_EXISTING_OFFSET, ALL_TAGS_NON_EXISTING_LIMIT);
		assertTrue(tags.getPage().isEmpty());
	}

	@Test
	public void getTagByNameShouldReturnOptionalWithTagWhenPassedExistingTagName() {
		Optional<Tag> optional = tagRepository.getTagByName(existingTag1.getName());
		assertEquals(existingTag1, optional.get());
	}

	@Test
	public void getTagByNameShouldReturnEmptyOptionalWhenPassedNonExistingTagName() {
		Optional<Tag> optional = tagRepository.getTagByName(nonExistingTag.getName());
		assertTrue(optional.isEmpty());
	}

	@Test
	public void deleteTagShouldReturnTrueWhenPassedExistingTagId() {
		boolean deleted = tagRepository.deleteTag(existingTag2.getId());
		assertTrue(deleted);
		Optional fetchedAfterDeletion = tagRepository.getTagById(existingTag2.getId());
		assertTrue(fetchedAfterDeletion.isEmpty());
	}

	@Test
	public void deleteTagShouldReturnFalseWhenPassedNonExistingTagId() {
		boolean deleted = tagRepository.deleteTag(nonExistingTag.getId());
		assertFalse(deleted);
	}

	@Test
	public void getTagByIdShouldReturnOptionalWithTagWhenPassedExistingTagId() {
		Optional<Tag> optional = tagRepository.getTagById(existingTag3.getId());
		assertEquals(existingTag3, optional.get());
	}

	@Test
	public void getTagByNameShouldReturnEmptyOptionalWhenPassedNonExistingTagId() {
		Optional<Tag> optional = tagRepository.getTagById(nonExistingTag.getId());
		assertTrue(optional.isEmpty());
	}

	@Test
	public void createTagShouldReturnTagWhenPassedNonExistingTagName() {
		Tag created = tagRepository.createTag(nonExistingTagToBeCreated);
		assertNotNull(created);
		Optional<Tag> fetchedAfterCreation = tagRepository.getTagById(created.getId());
		assertTrue(fetchedAfterCreation.isPresent());
		assertEquals(fetchedAfterCreation.get(), created);
	}

	@Test
	public void createTagShouldThrowExceptionWhenPassedExistingTagName() {

		assertThrows(DataIntegrityViolationException.class, () -> tagRepository.createTag(existingTagWithoutId));
	}

	@Test
	public void getTagsFromNameSetShouldReturnListWhenPassedAtLeastOneExistingTagName() {
		Set<String> tagNames = Set.of(existingTag1.getName(), existingTag8.getName(), nonExistingTag.getName());
		List<Tag> output = tagRepository.getTagsFromNameSet(tagNames);
		List<Tag> expected = List.of(existingTag1, existingTag8);
		assertEquals(expected, output);
	}

	@Test
	public void getMostWidelyUsedTagOfUserWithHighestCostOfAllOrdersShouldReturnCorrectTagWhenPassedExistingUser() {
		Optional<Tag> tag = tagRepository.getMostWidelyUsedTagOfUserWithHighestCostOfAllOrders(EXISTING_USER_ID);
		assertTrue(tag.isPresent());
		assertEquals(existingTag8, tag.get());
	}
}
