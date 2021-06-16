package com.epam.esm.service.impl;

import com.epam.esm.db.TagRepository;
import com.epam.esm.model.dto.TagDTO;
import com.epam.esm.model.entity.Tag;
import com.epam.esm.service.TagService;
import com.epam.esm.service.converter.Converter;
import com.epam.esm.service.exception.InvalidTagException;
import com.epam.esm.service.validator.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.Mockito;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.Collections.EMPTY_SET;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestInstance(Lifecycle.PER_CLASS)
public class TagServiceImplTest {
	private static final int ID = -1;
	private static final int MOCK_DB_ID = 2;
	private static final int MOCK_DB_ID_2 = 3;
	private static final String TAG_NAME = "tag name";
	private static final String TAG_NAME_2 = "tag name 2";
	private static final String ALREADY_EXISTS_MESSAGE_FIELD_NAME = "alreadyExistsExceptionTemplate";
	private static final String NOT_FOUND_MESSAGE_FIELD_NAME = "notFoundExceptionTemplate";
	private static final String MOCK_EX_MESSAGE = "test";
	private static final int EXISTING_CERT_ID = 1;
	private static final int NON_EXISTING_CERT_ID = 1;
	private Converter<TagDTO, Tag> tagDtoToTagConverter = Mockito.mock(Converter.class);
	private Converter<Tag, TagDTO> tagToTagDtoConverter = Mockito.mock(Converter.class);

	@BeforeAll
	private void init() {
		Mockito.when(tagDtoToTagConverter.convert(Mockito.any())).then(answer -> {
			TagDTO tagDTO = (TagDTO) answer.getArgument(0);
			return new Tag(tagDTO.getId(), tagDTO.getName());
		});
		Mockito.when(tagToTagDtoConverter.convert(Mockito.any())).then(answer -> {
			Tag tag = (Tag) answer.getArgument(0);
			return new TagDTO(tag.getId(), tag.getName());
		});
	}

	@Test
	public void createTagShouldReturnNewDtoWithIdWhenPassedCorrectDto() {
		Validator<TagDTO> tagValidator = Mockito.mock(Validator.class);
		TagDTO dto = new TagDTO(ID, TAG_NAME);
		TagRepository tagRepository = Mockito.mock(TagRepository.class);
		Mockito.when(tagRepository.createTag(Mockito.argThat(arg -> arg.getName().equals(TAG_NAME))))
				.thenReturn(new Tag(MOCK_DB_ID, TAG_NAME));
		TagService tagService =
				new TagServiceImpl(tagRepository, tagValidator, tagDtoToTagConverter, tagToTagDtoConverter);
		assertDoesNotThrow(() -> {
			TagDTO newDto = tagService.createTag(dto);
			assertNotSame(dto, newDto);
			assertEquals(MOCK_DB_ID, newDto.getId());
			assertEquals(TAG_NAME, newDto.getName());
		});
	}

	@Test
	public void createTagShouldThrowExceptionWhenPassedDtoWithInvalidName() {
		Validator<TagDTO> tagValidator = Mockito.mock(Validator.class);
		TagDTO tag = new TagDTO(ID, TAG_NAME);
		Mockito.doThrow(InvalidTagException.class).when(tagValidator)
				.validate(Mockito.argThat(a -> a.getName().equals(TAG_NAME)));
		TagRepository tagRepository = Mockito.mock(TagRepository.class);
		Mockito.when(tagRepository.createTag(Mockito.argThat(arg -> arg.getName().equals(TAG_NAME))))
				.thenReturn(new Tag(MOCK_DB_ID, TAG_NAME));
		TagService tagService =
				new TagServiceImpl(tagRepository, tagValidator, tagDtoToTagConverter, tagToTagDtoConverter);
		assertThrows(InvalidTagException.class, () -> tagService.createTag(tag));
	}

	@Test
	public void createTagShouldThrowExceptionWhenPassedDtoWithNameAlreadyExistingInDatabase() {
		Validator<TagDTO> tagValidator = Mockito.mock(Validator.class);
		TagDTO tag = new TagDTO(ID, TAG_NAME);
		TagRepository tagRepository = Mockito.mock(TagRepository.class);
		Mockito.doThrow(DuplicateKeyException.class).when(tagRepository)
				.createTag(Mockito.argThat(t -> t.getName().equals(TAG_NAME)));
		TagService tagService =
				new TagServiceImpl(tagRepository, tagValidator, tagDtoToTagConverter, tagToTagDtoConverter);
		ReflectionTestUtils.setField(tagService, ALREADY_EXISTS_MESSAGE_FIELD_NAME, MOCK_EX_MESSAGE, String.class);
		InvalidTagException ex = assertThrows(InvalidTagException.class, () -> tagService.createTag(tag));
		assertEquals(InvalidTagException.Reason.ALREADY_EXISTS, ex.getReason());
		assertEquals(TAG_NAME, ex.getTagName());
	}

	@Test
	public void getTagShouldReturnDtoWhenPassedExistingTagId() {
		Validator<TagDTO> tagValidator = Mockito.mock(Validator.class);
		Tag tagFromDb = new Tag(MOCK_DB_ID, TAG_NAME);
		TagRepository tagRepository = Mockito.mock(TagRepository.class);
		Mockito.when(tagRepository.getTagById(Mockito.eq(MOCK_DB_ID))).thenReturn(Optional.of(tagFromDb));
		TagService tagService =
				new TagServiceImpl(tagRepository, tagValidator, tagDtoToTagConverter, tagToTagDtoConverter);
		assertDoesNotThrow(() -> {
			TagDTO res = tagService.getTag(MOCK_DB_ID);
			assertEquals(MOCK_DB_ID, res.getId());
			assertEquals(TAG_NAME, res.getName());
		});
	}

	@Test
	public void getTagShouldThrowExceptionWhenPassedNonExistingTagId() {
		Validator<TagDTO> tagValidator = Mockito.mock(Validator.class);
		TagRepository tagRepository = Mockito.mock(TagRepository.class);
		Mockito.when(tagRepository.getTagById(Mockito.eq(MOCK_DB_ID))).thenReturn(Optional.empty());
		TagService tagService =
				new TagServiceImpl(tagRepository, tagValidator, tagDtoToTagConverter, tagToTagDtoConverter);
		ReflectionTestUtils.setField(tagService, NOT_FOUND_MESSAGE_FIELD_NAME, MOCK_EX_MESSAGE, String.class);
		InvalidTagException ex = assertThrows(InvalidTagException.class, () -> tagService.getTag(MOCK_DB_ID));
		assertEquals(InvalidTagException.Reason.NOT_FOUND, ex.getReason());
		assertEquals(MOCK_DB_ID, ex.getTagId());
	}

	@Test
	public void deleteTagShouldNotThrowExceptionWhenPassedExistingId() {
		Validator<TagDTO> tagValidator = Mockito.mock(Validator.class);
		TagRepository tagRepository = Mockito.mock(TagRepository.class);
		Mockito.when(tagRepository.deleteTag(Mockito.eq(MOCK_DB_ID))).thenReturn(true);
		TagService tagService =
				new TagServiceImpl(tagRepository, tagValidator, tagDtoToTagConverter, tagToTagDtoConverter);
		assertDoesNotThrow(() -> tagService.deleteTag(MOCK_DB_ID));
	}

	@Test
	public void deleteTagShouldThrowExceptionWhenPassedNonExistingId() {
		Validator<TagDTO> tagValidator = Mockito.mock(Validator.class);
		TagRepository tagRepository = Mockito.mock(TagRepository.class);
		Mockito.when(tagRepository.deleteTag(Mockito.eq(MOCK_DB_ID))).thenReturn(false);
		TagService tagService =
				new TagServiceImpl(tagRepository, tagValidator, tagDtoToTagConverter, tagToTagDtoConverter);
		ReflectionTestUtils.setField(tagService, NOT_FOUND_MESSAGE_FIELD_NAME, MOCK_EX_MESSAGE, String.class);
		InvalidTagException ex = assertThrows(InvalidTagException.class, () -> tagService.deleteTag(MOCK_DB_ID));
		assertEquals(InvalidTagException.Reason.NOT_FOUND, ex.getReason());
		assertEquals(MOCK_DB_ID, ex.getTagId());
	}

	@Test
	public void getAllTagsShouldNotThrowExceptionAndReturnList() {
		Validator<TagDTO> tagValidator = Mockito.mock(Validator.class);
		Tag tag1 = new Tag(MOCK_DB_ID, TAG_NAME);
		TagDTO dto1 = new TagDTO(MOCK_DB_ID, TAG_NAME);
		Tag tag2 = new Tag(MOCK_DB_ID_2, TAG_NAME_2);
		TagDTO dto2 = new TagDTO(MOCK_DB_ID_2, TAG_NAME_2);
		List<Tag> tags = new ArrayList<>();
		List<TagDTO> expectedOutput = new ArrayList<>();
		tags.add(tag1);
		expectedOutput.add(dto1);
		tags.add(tag2);
		expectedOutput.add(dto2);
		TagRepository tagRepository = Mockito.mock(TagRepository.class);
		Mockito.when(tagRepository.getAllTags()).thenReturn(tags);
		TagService tagService =
				new TagServiceImpl(tagRepository, tagValidator, tagDtoToTagConverter, tagToTagDtoConverter);
		assertDoesNotThrow(() -> {
			List<TagDTO> tagDTOs = tagService.getAllTags();
			assertEquals(expectedOutput, tagDTOs);
		});
	}

	@Test
	public void getAllTagsShouldNotThrowExceptionWhenPassedExistingId() {
		Validator<TagDTO> tagValidator = Mockito.mock(Validator.class);
		Tag tag1 = new Tag(MOCK_DB_ID, TAG_NAME);
		TagDTO dto1 = new TagDTO(MOCK_DB_ID, TAG_NAME);
		Tag tag2 = new Tag(MOCK_DB_ID_2, TAG_NAME_2);
		TagDTO dto2 = new TagDTO(MOCK_DB_ID_2, TAG_NAME_2);
		List<Tag> tags = new ArrayList<>();
		Set<TagDTO> expectedOutput = new HashSet<>();
		tags.add(tag1);
		expectedOutput.add(dto1);
		tags.add(tag2);
		expectedOutput.add(dto2);
		TagRepository tagRepository = Mockito.mock(TagRepository.class);
		Mockito.when(tagRepository.getTagsByCertificate(Mockito.eq(EXISTING_CERT_ID))).thenReturn(tags);
		TagService tagService =
				new TagServiceImpl(tagRepository, tagValidator, tagDtoToTagConverter, tagToTagDtoConverter);
		assertDoesNotThrow(() -> {
			Set<TagDTO> tagDTOs = tagService.getTagsByCertificate(EXISTING_CERT_ID);
			assertEquals(expectedOutput, tagDTOs);
		});
	}

	@Test
	public void getTagsFromNameSetShouldReturnEmptyTagDtoSetWhenPassedEmptyNameSet() {
		Validator<TagDTO> tagValidator = Mockito.mock(Validator.class);
		TagRepository tagRepository = Mockito.mock(TagRepository.class);
		TagService tagService =
				new TagServiceImpl(tagRepository, tagValidator, tagDtoToTagConverter, tagToTagDtoConverter);
		assertDoesNotThrow(() -> {
			Set<TagDTO> set = tagService.getTagsFromNameSet(new HashSet<String>());
			assertEquals(EMPTY_SET, set);
		});
	}
}
