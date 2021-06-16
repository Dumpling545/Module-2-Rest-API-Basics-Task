package com.epam.esm.db;

import com.epam.esm.model.entity.Tag;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = DatabaseTestConfig.class)
@ActiveProfiles("test")
@TestInstance(Lifecycle.PER_CLASS)
public class TagRepositoryTest {
	private static final int EXISTING_TAG_ID_1 = 1;
	private static final String EXISTING_TAG_NAME_1 = "tag1";
	private static final int EXISTING_TAG_ID_2 = 2;
	private static final String EXISTING_TAG_NAME_2 = "tag2";
	private static final int EXISTING_TAG_ID_3 = 3;
	private static final String EXISTING_TAG_NAME_3 = "tag3";
	private static final int EXISTING_TAG_ID_4 = 4;
	private static final String EXISTING_TAG_NAME_4 = "tag4";
	private static final int EXISTING_TAG_ID_8 = 8;
	private static final String EXISTING_TAG_NAME_8 = "tag8";
	private static final int EXISTING_TAG_ID_9 = 9;
	private static final String EXISTING_TAG_NAME_9 = "tag9";
	private static final int EXISTING_TAG_ID_10 = 10;
	private static final String EXISTING_TAG_NAME_10 = "tag10";
	private static final int EXISTING_CERT_ID_1 = 1;
	private static final int NON_EXISTING_TAG_ID = -1;
	private static final int NON_EXISTING_CERTIFICATE_ID = -1;
	private static final String NON_EXISTING_TAG_NAME = "tag0";
	private static final String NON_EXISTING_TAG_NAME_TO_BE_CREATED = "tag100";
	@Autowired
	TagRepository tagRepository;
	private List<Tag> expectedTagListOfCertificateOne = new ArrayList<>();
	@BeforeAll
	public void initExpectedTagListOfCertificateOne() {
		expectedTagListOfCertificateOne.add(new Tag(EXISTING_TAG_ID_8, EXISTING_TAG_NAME_8));
		expectedTagListOfCertificateOne.add(new Tag(EXISTING_TAG_ID_9, EXISTING_TAG_NAME_9));
		expectedTagListOfCertificateOne.add(new Tag(EXISTING_TAG_ID_10, EXISTING_TAG_NAME_10));
	}

	@Test
	public void getTagByNameShouldReturnOptionalWithTagWhenPassedExistingTagName() {
		Optional<Tag> optional = tagRepository.getTagByName(EXISTING_TAG_NAME_1);
		Tag expected = new Tag(EXISTING_TAG_ID_1, EXISTING_TAG_NAME_1);
		assertEquals(expected, optional.get());
	}

	@Test
	public void getTagByNameShouldReturnEmptyOptionalWhenPassedNonExistingTagName() {
		Optional<Tag> optional = tagRepository.getTagByName(NON_EXISTING_TAG_NAME);
		assertTrue(optional.isEmpty());
	}

	@Test
	public void deleteTagShouldReturnTrueWhenPassedExistingTagId() {
		boolean deleted = tagRepository.deleteTag(EXISTING_TAG_ID_2);
		assertTrue(deleted);
	}

	@Test
	public void deleteTagShouldReturnFalseWhenPassedNonExistingTagId() {
		boolean deleted = tagRepository.deleteTag(NON_EXISTING_TAG_ID);
		assertFalse(deleted);
	}

	@Test
	public void getTagByIdShouldReturnOptionalWithTagWhenPassedExistingTagName() {
		Optional<Tag> optional = tagRepository.getTagById(EXISTING_TAG_ID_3);
		Tag expected = new Tag(EXISTING_TAG_ID_3, EXISTING_TAG_NAME_3);
		assertEquals(expected, optional.get());
	}

	@Test
	public void getTagByNameShouldReturnEmptyOptionalWhenPassedNonExistingTagId() {
		Optional<Tag> optional = tagRepository.getTagById(NON_EXISTING_TAG_ID);
		assertTrue(optional.isEmpty());
	}

	@Test
	public void createTagShouldReturnTagWhenPassedNonExistingTagName() {
		Tag tag = new Tag(NON_EXISTING_TAG_ID, NON_EXISTING_TAG_NAME_TO_BE_CREATED);
		Tag created = tagRepository.createTag(tag);
		assertNotNull(created);
	}

	@Test
	public void createTagShouldThrowExceptionWhenPassedExistingTagName() {
		Tag tag = new Tag(NON_EXISTING_TAG_ID, EXISTING_TAG_NAME_4);
		assertThrows(DuplicateKeyException.class, () -> tagRepository.createTag(tag));
	}

	@Test
	public void getTagsByCertificateShouldReturnListWhenPassedExistingCertificateId() {
		List<Tag> tags = tagRepository.getTagsByCertificate(EXISTING_CERT_ID_1);
		assertEquals(expectedTagListOfCertificateOne, tags);
	}
	@Test
	public void getTagsByCertificateShouldReturnEmptyListWhenPassedNonExistingCertificateId() {
		List<Tag> tags = tagRepository.getTagsByCertificate(NON_EXISTING_CERTIFICATE_ID);
		assertTrue(tags.isEmpty());
	}
	@Test
	public void getTagsFromNameSetShouldReturnListWhenPassedAtLeastOneExistingTagName() {
		Set<String> tagNames = new HashSet<>();
		tagNames.add(EXISTING_TAG_NAME_1);
		tagNames.add(EXISTING_TAG_NAME_2);
		tagNames.add(NON_EXISTING_TAG_NAME);
		List<Tag> output = tagRepository.getTagsFromNameSet(tagNames);
		List<Tag> expected = new ArrayList<>();
		expected.add(new Tag(EXISTING_TAG_ID_1, EXISTING_TAG_NAME_1));
		expected.add(new Tag(EXISTING_TAG_ID_2, EXISTING_TAG_NAME_2));
		assertEquals(expected, output);
	}
}
