package com.epam.esm.db;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = DatabaseTestConfig.class)
@ActiveProfiles("test")
public class TagRepositoryTest {
	@Autowired
	TagRepository tagRepository;
/*
	@Test
	public void getTagByNamePositiveTest() {
		Optional<Tag> optional = tagRepository.getTagByName("tag1");
		assertEquals(optional.isEmpty(), false);
		assertEquals(optional.get().getName(), "tag1");
		assertNotEquals(optional.get().getId(), -1);
	}

	@Test
	public void getTagByNameNegativeTest() {
		Optional<Tag> optional = tagRepository.getTagByName("tag0");
		assertEquals(optional.isEmpty(), true);
	}

	@Test
	public void deleteTagPositiveTest() {
		Optional<Tag> optional = tagRepository.getTagByName("tag2");
		assertEquals(optional.isEmpty(), false);
		assertEquals(optional.get().getName(), "tag2");
		assertNotEquals(optional.get().getId(), -1);
		int id = optional.get().getId();
		boolean deleted = tagRepository.deleteTag(id);
		assertTrue(deleted);
		optional = tagRepository.getTagByName("tag2");
		assertEquals(optional.isEmpty(), true);
		optional = tagRepository.getTagById(id);
		assertEquals(optional.isEmpty(), true);
	}

	@Test
	public void deleteTagNegativeTest() {
		boolean deleted = tagRepository.deleteTag(-1);
		assertFalse(deleted);
	}

	@Test
	public void getTagByIdPositiveTest() {
		Optional<Tag> optional = tagRepository.getTagById(3);
		assertFalse(optional.isEmpty());
	}

	@Test
	public void getTagByIdNegativeTest() {
		Optional<Tag> optional = tagRepository.getTagById(-1);
		assertTrue(optional.isEmpty());
	}

	@Test
	public void createTagPositiveTest() {
		Tag tag = new Tag("tag11");
		tagRepository.createTag(tag);
		assertNotEquals(tag.getId(), -1);
		Optional<Tag> optional = tagRepository.getTagByName("tag11");
		assertFalse(optional.isEmpty());
		assertEquals(tag, optional.get());
	}

	@Test
	public void createTagAlreadyExistsTest() {
		Tag tag = new Tag("tag4");
		assertThrows(DuplicateKeyException.class, () -> tagRepository.createTag(tag));
	}

	@Test
	public void getTagsByCertificatePositiveTest() {
		List<Tag> tags = tagRepository.getTagsByCertificate(1);
		assertEquals(tags.size(), 3);
		tags.removeIf(t -> t.getId() == 8);
		tags.removeIf(t -> t.getId() == 9);
		tags.removeIf(t -> t.getId() == 10);
		assertEquals(tags.size(), 0);
	}

	@Test
	public void getTagsByCertificateNegativeTest() {
		List<Tag> tags = tagRepository.getTagsByCertificate(-1);
		assertEquals(tags.size(), 0);

	}*/
}
