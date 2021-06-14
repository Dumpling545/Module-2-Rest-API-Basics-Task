package com.epam.esm.service.impl;

import com.epam.esm.db.TagRepository;
import com.epam.esm.model.dto.TagDTO;
import com.epam.esm.model.entity.Tag;
import com.epam.esm.service.TagService;
import com.epam.esm.service.exception.InvalidTagNameException;
import com.epam.esm.service.exception.TagAlreadyExistsException;
import com.epam.esm.service.exception.TagNotFoundException;
import com.epam.esm.service.validator.TagValidator;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.dao.DuplicateKeyException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class TagServiceImplTest {


	@Test
	public void createTagPositiveTest() {
		int newDatabaseId = 1;
		TagValidator tagValidator = Mockito.mock(TagValidator.class);
		TagDTO tag = new TagDTO("correctName");
		TagRepository tagRepository = Mockito.mock(TagRepository.class);
		Mockito.doAnswer(invocation -> {
			((Tag) invocation.getArgument(0)).setId(newDatabaseId);
			return null;
		}).when(tagRepository).createTag(Mockito.eq(tag));
		TagService tagService = new TagServiceImpl(tagRepository, tagValidator);
		assertDoesNotThrow(() -> tagService.createTag(tag));
		assertEquals(newDatabaseId, tag.getId());
	}

	@Test
	public void createInvalidTagTest() {
		int newDatabaseId = 1;
		TagValidator tagValidator = Mockito.mock(TagValidator.class);
		TagDTO tag = new TagDTO("invalidName");
		Mockito.doThrow(InvalidTagNameException.class).when(tagValidator).validateTag(Mockito.eq(tag));
		TagRepository tagRepository = Mockito.mock(TagRepository.class);
		Mockito.doAnswer(invocation -> {
			((Tag) invocation.getArgument(0)).setId(newDatabaseId);
			return null;
		}).when(tagRepository).createTag(Mockito.eq(tag));
		TagService tagService = new TagServiceImpl(tagRepository, tagValidator);
		assertThrows(InvalidTagNameException.class, () -> tagService.createTag(tag));
	}

	@Test
	public void createDuplicateTagTest() {
		int newDatabaseId = 1;
		String duplicate = "tagName";
		TagValidator tagValidator = Mockito.mock(TagValidator.class);
		TagDTO tag = new TagDTO(duplicate);
		TagRepository tagRepository = Mockito.mock(TagRepository.class);
		Mockito.doThrow(DuplicateKeyException.class).when(tagRepository)
				.createTag(Mockito.argThat(t -> t.getName().equals(duplicate)));
		TagService tagService = new TagServiceImpl(tagRepository, tagValidator);
		assertThrows(TagAlreadyExistsException.class, () -> tagService.createTag(tag));
	}

	@Test
	public void getTagPositiveTest() {
		Integer id = 1;
		TagValidator tagValidator = Mockito.mock(TagValidator.class);
		Tag tag = new Tag(id, "correctName");
		TagRepository tagRepository = Mockito.mock(TagRepository.class);
		Mockito.when(tagRepository.getTagById(Mockito.eq(id))).thenReturn(Optional.of(tag));
		TagService tagService = new TagServiceImpl(tagRepository, tagValidator);
		assertDoesNotThrow(() -> {
			TagDTO res = tagService.getTag(id);
			assertEquals(res.getId(), tag.getId());
			assertEquals(res.getName(), tag.getName());
		});
	}

	@Test
	public void getUnexistingTagTest() {
		Integer id = 1;
		TagValidator tagValidator = Mockito.mock(TagValidator.class);
		Tag tag = new Tag(id, "correctName");
		TagRepository tagRepository = Mockito.mock(TagRepository.class);
		Mockito.when(tagRepository.getTagById(Mockito.eq(id))).thenReturn(Optional.empty());
		TagService tagService = new TagServiceImpl(tagRepository, tagValidator);
		TagNotFoundException ex = assertThrows(TagNotFoundException.class, () -> tagService.getTag(id));
		assertEquals(ex.getId(), id);
	}

	@Test
	public void deleteTagPositiveTest() {
		Integer id = 1;
		TagValidator tagValidator = Mockito.mock(TagValidator.class);
		Tag tag = new Tag(id, "correctName");
		TagRepository tagRepository = Mockito.mock(TagRepository.class);
		Mockito.when(tagRepository.deleteTag(Mockito.eq(id))).thenReturn(true);
		TagService tagService = new TagServiceImpl(tagRepository, tagValidator);
		assertDoesNotThrow(() -> tagService.deleteTag(id));
	}

	@Test
	public void deleteUnexistingTagTest() {
		Integer id = 1;
		TagValidator tagValidator = Mockito.mock(TagValidator.class);
		Tag tag = new Tag(id, "correctName");
		TagRepository tagRepository = Mockito.mock(TagRepository.class);
		Mockito.when(tagRepository.deleteTag(Mockito.eq(id))).thenReturn(false);
		TagService tagService = new TagServiceImpl(tagRepository, tagValidator);
		TagNotFoundException ex = assertThrows(TagNotFoundException.class, () -> tagService.deleteTag(id));
		assertEquals(ex.getId(), id);
	}

	@Test
	public void getAllTagsPositiveTest() {
		TagValidator tagValidator = Mockito.mock(TagValidator.class);
		Tag tag1 = new Tag(1, "correctName1");
		Tag tag2 = new Tag(2, "correctName2");
		List<Tag> tags = new ArrayList<>();
		tags.add(tag1);
		tags.add(tag2);
		TagRepository tagRepository = Mockito.mock(TagRepository.class);
		Mockito.when(tagRepository.getAllTags()).thenReturn(tags);
		TagService tagService = new TagServiceImpl(tagRepository, tagValidator);
		assertDoesNotThrow(() -> {
			List<TagDTO> tagDTOs = tagService.getAllTags();
			for (int i = 0; i < tagDTOs.size(); i++) {
				assertEquals(tagDTOs.get(i).getId(), tags.get(i).getId());
				assertEquals(tagDTOs.get(i).getName(), tags.get(i).getName());
			}
		});
	}

}
