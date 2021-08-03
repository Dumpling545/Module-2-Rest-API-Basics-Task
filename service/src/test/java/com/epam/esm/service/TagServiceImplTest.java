package com.epam.esm.service;

import com.epam.esm.db.TagRepository;
import com.epam.esm.model.dto.TagDTO;
import com.epam.esm.model.entity.Tag;
import com.epam.esm.service.converter.TagConverter;
import com.epam.esm.service.exception.InvalidTagException;
import com.epam.esm.service.impl.TagServiceImpl;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.data.util.TypeInformation;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.Collections.EMPTY_LIST;
import static java.util.Collections.EMPTY_SET;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TagServiceImplTest {
	private static final int ID = -1;
	private static final int MOCK_DB_ID_2 = 3;
	private static final TagDTO tagDtoToBeCreated = TagDTO.builder().name("tag name").build();
	private static final Tag mockedCreatedTag = Tag.builder()
			.id(10)
			.name(tagDtoToBeCreated.getName()).build();
	private static final TagDTO mockedCreatedTagDto = TagDTO.builder()
			.id(mockedCreatedTag.getId())
			.name(mockedCreatedTag.getName()).build();
	private static final TagDTO tagDtoToBeCreated2 = TagDTO.builder().name("tag name 2").build();
	private static final Tag mockedCreatedTag2 = Tag.builder()
			.id(11)
			.name(tagDtoToBeCreated2.getName()).build();
	private static final TagDTO mockedCreatedTagDto2 = TagDTO.builder()
			.id(mockedCreatedTag2.getId())
			.name(mockedCreatedTag2.getName()).build();
	private static final TagDTO existingTagDto1 = TagDTO.builder()
			.id(999)
			.name("tag").build();
	private static final Tag existingTag1 = Tag.builder()
			.id(existingTagDto1.getId())
			.name(existingTagDto1.getName()).build();
	private static final TagDTO existingTagDto2 = TagDTO.builder()
			.id(919)
			.name("tag2").build();
	private static final Tag existingTag2 = Tag.builder()
			.id(existingTagDto2.getId())
			.name(existingTagDto2.getName()).build();
	private static final List<Tag> allExistingTagList = List.of(existingTag1, existingTag2);
	private static final Slice<Tag> tagSlice = new SliceImpl<>(allExistingTagList);
	private static final List<TagDTO> allExistingTagDtoList = List.of(existingTagDto1, existingTagDto2);
	private static final Slice<TagDTO> tagDtoSlice = new SliceImpl<>(allExistingTagDtoList);
	private static final TagDTO nonExistingTagDto = TagDTO.builder()
			.id(-10)
			.name("non existent").build();
	private static final String TAG_NAME_2 = "tag name 2";
	private static final String ALREADY_EXISTS_MESSAGE_FIELD_NAME = "alreadyExistsExceptionTemplate";
	private static final String NOT_FOUND_MESSAGE_FIELD_NAME = "notFoundExceptionTemplate";
	private static final String MOCK_EX_MESSAGE = "test";
	private static final int EXISTING_CERT_ID = 1;
	private static final int NON_EXISTING_CERT_ID = 1;
	private static final Pageable pageable = Pageable.ofSize(5);
	private static final String INVALID_FIELD_TOKEN_TEMPLATE_FIELD_NAME = "invalidFieldTokenTemplate";
	private final TagConverter tagConverter = Mappers.getMapper(TagConverter.class);

	@Test
	public void createTagShouldReturnNewDtoWithIdWhenPassedCorrectDto() {
		TagRepository tagRepository = Mockito.mock(TagRepository.class);
		Mockito.when(tagRepository.save(Mockito.argThat(arg -> arg.getName().equals(tagDtoToBeCreated.getName()))))
				.thenReturn(mockedCreatedTag);
		TagService tagService = new TagServiceImpl(tagRepository, tagConverter);
		assertDoesNotThrow(() -> {
			TagDTO newDto = tagService.createTag(tagDtoToBeCreated);
			assertNotSame(tagDtoToBeCreated, newDto);
			assertEquals(mockedCreatedTagDto, newDto);
		});
	}

	@Test
	public void createTagShouldThrowExceptionWhenInnerDataIntegrityViolationExceptionIsThrown() {
		TagRepository tagRepository = Mockito.mock(TagRepository.class);
		Mockito.doThrow(DataIntegrityViolationException.class).when(tagRepository)
				.save(Mockito.argThat(arg -> arg.getName().equals(tagDtoToBeCreated.getName())));
		TagService tagService = new TagServiceImpl(tagRepository, tagConverter);
		ReflectionTestUtils.setField(tagService, ALREADY_EXISTS_MESSAGE_FIELD_NAME, MOCK_EX_MESSAGE, String.class);
		InvalidTagException ex = assertThrows(InvalidTagException.class, () -> tagService.createTag(tagDtoToBeCreated));
		assertEquals(InvalidTagException.Reason.ALREADY_EXISTS, ex.getReason());
	}

	@Test
	public void createTagsShouldReturnNewDtoWithIdWhenPassedCorrectDto() {
		TagRepository tagRepository = Mockito.mock(TagRepository.class);
		Mockito.when(tagRepository.saveAll(Mockito.any()))
				.thenReturn(Set.of(mockedCreatedTag, mockedCreatedTag2));
		TagService tagService = new TagServiceImpl(tagRepository, tagConverter);
		assertDoesNotThrow(() -> {
			Set<TagDTO> newDtos = tagService.createTags(Set.of(tagDtoToBeCreated, tagDtoToBeCreated2));
			assertEquals(Set.of(mockedCreatedTagDto, mockedCreatedTagDto2), newDtos);
		});
	}

	@Test
	public void createTagsShouldThrowInvalidTagExceptionWhenInnerDataIntegrityViolationExceptionIsThrown() {
		TagRepository tagRepository = Mockito.mock(TagRepository.class);
		Mockito.doThrow(DataIntegrityViolationException.class).when(tagRepository)
				.saveAll(Mockito.any());
		TagService tagService = new TagServiceImpl(tagRepository, tagConverter);
		ReflectionTestUtils.setField(tagService, ALREADY_EXISTS_MESSAGE_FIELD_NAME, MOCK_EX_MESSAGE, String.class);
		InvalidTagException ex = assertThrows(InvalidTagException.class,
		                                      () -> tagService.createTags(Set.of(tagDtoToBeCreated)));
		assertEquals(InvalidTagException.Reason.ALREADY_EXISTS, ex.getReason());
	}

	@Test
	public void getTagShouldReturnDtoWhenPassedExistingTagId() {
		TagRepository tagRepository = Mockito.mock(TagRepository.class);
		Mockito.when(tagRepository.findById(Mockito.eq(existingTagDto1.getId())))
				.thenReturn(Optional.of(existingTag1));
		TagService tagService = new TagServiceImpl(tagRepository, tagConverter);
		assertDoesNotThrow(() -> {
			TagDTO res = tagService.getTag(existingTagDto1.getId());
			assertEquals(existingTagDto1, res);
		});
	}

	@Test
	public void getTagShouldThrowExceptionWhenPassedNonExistingTagId() {
		TagRepository tagRepository = Mockito.mock(TagRepository.class);
		Mockito.when(tagRepository.findById(Mockito.eq(nonExistingTagDto.getId()))).thenReturn(Optional.empty());
		TagService tagService = new TagServiceImpl(tagRepository, tagConverter);
		ReflectionTestUtils.setField(tagService, NOT_FOUND_MESSAGE_FIELD_NAME, MOCK_EX_MESSAGE, String.class);
		InvalidTagException ex =
				assertThrows(InvalidTagException.class,
		                                      () -> tagService.getTag(existingTagDto1.getId()));
		assertEquals(InvalidTagException.Reason.NOT_FOUND, ex.getReason());
	}

	@Test
	public void getMostWidelyUsedTagOfUserWithHighestCostOfAllOrdersShouldReturnDtoWhenPassedExistingTagId() {
		TagRepository tagRepository = Mockito.mock(TagRepository.class);
		Mockito.when(
						tagRepository.getMostWidelyUsedTagOfUserWithHighestCostOfAllOrders(Mockito.eq(existingTagDto1.getId())))
				.thenReturn(Optional.of(existingTag1));
		TagService tagService = new TagServiceImpl(tagRepository, tagConverter);
		assertDoesNotThrow(() -> {
			TagDTO res = tagService.getMostWidelyUsedTagOfUserWithHighestCostOfAllOrders(existingTagDto1.getId());
			assertEquals(existingTagDto1, res);
		});
	}

	@Test
	public void getMostWidelyUsedTagOfUserWithHighestCostOfAllOrdersShouldThrowExceptionWhenPassedNonExistingTagId() {
		TagRepository tagRepository = Mockito.mock(TagRepository.class);
		Mockito.when(tagRepository.getMostWidelyUsedTagOfUserWithHighestCostOfAllOrders(
						Mockito.eq(nonExistingTagDto.getId())))
				.thenReturn(Optional.empty());
		TagService tagService = new TagServiceImpl(tagRepository, tagConverter);
		ReflectionTestUtils.setField(tagService, NOT_FOUND_MESSAGE_FIELD_NAME, MOCK_EX_MESSAGE, String.class);
		InvalidTagException ex = assertThrows(InvalidTagException.class,
		                                      () -> tagService.getMostWidelyUsedTagOfUserWithHighestCostOfAllOrders(
				                                      existingTagDto1.getId()));
		assertEquals(InvalidTagException.Reason.NOT_FOUND, ex.getReason());
	}

	@Test
	public void getTagByNameShouldReturnDtoWhenPassedExistingTagName() {
		TagRepository tagRepository = Mockito.mock(TagRepository.class);
		Mockito.when(tagRepository.getTagByName(Mockito.eq(existingTagDto1.getName())))
				.thenReturn(Optional.of(existingTag1));
		TagService tagService = new TagServiceImpl(tagRepository, tagConverter);
		assertDoesNotThrow(() -> {
			TagDTO res = tagService.getTag(existingTagDto1.getName());
			assertEquals(existingTagDto1, res);
		});
	}

	@Test
	public void getTagByNameShouldThrowExceptionWhenPassedNonExistingTagName() {
		TagRepository tagRepository = Mockito.mock(TagRepository.class);
		Mockito.when(tagRepository.getTagByName(Mockito.eq(nonExistingTagDto.getName()))).thenReturn(Optional.empty());
		TagService tagService = new TagServiceImpl(tagRepository, tagConverter);
		ReflectionTestUtils.setField(tagService, NOT_FOUND_MESSAGE_FIELD_NAME, MOCK_EX_MESSAGE, String.class);
		InvalidTagException ex =
				assertThrows(InvalidTagException.class,
		                                      () -> tagService.getTag(nonExistingTagDto.getName()));
		assertEquals(InvalidTagException.Reason.NOT_FOUND, ex.getReason());
	}

	@Test
	public void deleteTagShouldNotThrowExceptionWhenPassedExistingId() {
		TagRepository tagRepository = Mockito.mock(TagRepository.class);
		TagService tagService = new TagServiceImpl(tagRepository, tagConverter);
		assertDoesNotThrow(() -> tagService.deleteTag(existingTagDto1.getId()));
	}

	@Test
	public void deleteTagShouldThrowInvalidTagExceptionWhenInnerEmptyResultDataAccessExceptionIsThrown() {
		TagRepository tagRepository = Mockito.mock(TagRepository.class);
		TagService tagService = new TagServiceImpl(tagRepository, tagConverter);
		Mockito.doThrow(EmptyResultDataAccessException.class).when(tagRepository).deleteById(Mockito.anyInt());
		ReflectionTestUtils.setField(tagService, NOT_FOUND_MESSAGE_FIELD_NAME, MOCK_EX_MESSAGE, String.class);
		InvalidTagException ex =
				assertThrows(InvalidTagException.class,
		                                      () -> tagService.deleteTag(existingTagDto1.getId()));
		assertEquals(InvalidTagException.Reason.NOT_FOUND, ex.getReason());
	}

	@Test
	public void getAllTagsShouldNotThrowExceptionAndReturnList() {
		TagRepository tagRepository = Mockito.mock(TagRepository.class);
		TagService tagService = new TagServiceImpl(tagRepository, tagConverter);
		Mockito.when(tagRepository.getAllTagsBy(Mockito.any())).thenReturn(tagSlice);
		assertDoesNotThrow(() -> {
			Slice<TagDTO> res = tagService.getAllTags(pageable);
			assertEquals(tagDtoSlice, res);
		});
	}

	@Test
	public void getAllTagsShouldThrowInvalidTagExceptionWhenInnerPropertyReferenceExceptionIsThrown() {
		TagRepository tagRepository = Mockito.mock(TagRepository.class);
		TagService tagService = new TagServiceImpl(tagRepository, tagConverter);
		TypeInformation typeInformation = Mockito.mock(TypeInformation.class);
		PropertyReferenceException pre = new PropertyReferenceException(MOCK_EX_MESSAGE, typeInformation, EMPTY_LIST);
		Mockito.when(tagRepository.getAllTagsBy(Mockito.any())).thenThrow(pre);
		ReflectionTestUtils.setField(tagService, INVALID_FIELD_TOKEN_TEMPLATE_FIELD_NAME, MOCK_EX_MESSAGE,
		                             String.class);
		InvalidTagException ite = assertThrows(InvalidTagException.class, () -> tagService.getAllTags(pageable));
		assertEquals(InvalidTagException.Reason.INVALID_SORT_BY, ite.getReason());
	}

	@Test
	public void getTagsFromNameSetShouldReturnEmptyTagDtoSetWhenPassedEmptyNameSet() {
		TagRepository tagRepository = Mockito.mock(TagRepository.class);
		TagService tagService = new TagServiceImpl(tagRepository, tagConverter);
		assertDoesNotThrow(() -> {
			Set<TagDTO> set = tagService.getTagsFromNameSet(new HashSet<>());
			assertEquals(EMPTY_SET, set);
		});
	}

	@Test
	public void getTagsFromNameSetShouldReturnTagDtoSetWhenPassedCorrectNameSet() {
		TagRepository tagRepository = Mockito.mock(TagRepository.class);
		TagService tagService = new TagServiceImpl(tagRepository, tagConverter);
		Set tagNameSet = Set.of(existingTagDto1.getName(), nonExistingTagDto.getName());
		Mockito.when(tagRepository.getTagsByNameIn(Mockito.eq(tagNameSet)))
				.thenReturn(List.of(existingTag1));
		assertDoesNotThrow(() -> {
			Set<TagDTO> set = tagService.getTagsFromNameSet(tagNameSet);
			assertEquals(Set.of(existingTagDto1), set);
		});
	}
}
