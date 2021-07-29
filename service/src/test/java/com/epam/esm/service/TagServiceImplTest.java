package com.epam.esm.service;

import com.epam.esm.db.TagRepository;
import com.epam.esm.model.dto.PageDTO;
import com.epam.esm.model.dto.PagedResultDTO;
import com.epam.esm.model.dto.TagDTO;
import com.epam.esm.model.entity.PagedResult;
import com.epam.esm.model.entity.Tag;
import com.epam.esm.service.converter.PagedResultConverter;
import com.epam.esm.service.converter.TagConverter;
import com.epam.esm.service.exception.InvalidTagException;
import com.epam.esm.service.impl.TagServiceImpl;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
    private static final PagedResult<Tag> tagPage = PagedResult.<Tag>builder()
            .page(allExistingTagList).build();
    private static final List<TagDTO> allExistingTagDtoList = List.of(existingTagDto1, existingTagDto2);
    private static final PagedResultDTO<TagDTO> tagPageDto = PagedResultDTO.<TagDTO>builder()
			.page(allExistingTagDtoList).build();
    private static final TagDTO nonExistingTagDto = TagDTO.builder()
            .id(-10)
            .name("non existent").build();
    private static final String TAG_NAME_2 = "tag name 2";
    private static final String ALREADY_EXISTS_MESSAGE_FIELD_NAME = "alreadyExistsExceptionTemplate";
    private static final String NOT_FOUND_MESSAGE_FIELD_NAME = "notFoundExceptionTemplate";
    private static final String MOCK_EX_MESSAGE = "test";
    private static final int EXISTING_CERT_ID = 1;
    private static final int NON_EXISTING_CERT_ID = 1;
    private static final PageDTO pageDTO = PageDTO.builder().pageNumber(1).pageSize(5).build();
    private final TagConverter tagConverter = Mappers.getMapper(TagConverter.class);
    private final PagedResultConverter pagedResultConverter = Mappers.getMapper(PagedResultConverter.class);

    @Test
    public void createTagShouldReturnNewDtoWithIdWhenPassedCorrectDto() {
        TagRepository tagRepository = Mockito.mock(TagRepository.class);
        Mockito.when(tagRepository
                .createTag(Mockito.argThat(arg -> arg.getName().equals(tagDtoToBeCreated.getName()))))
                .thenReturn(mockedCreatedTag);
        TagService tagService = new TagServiceImpl(tagRepository, tagConverter, pagedResultConverter);
        assertDoesNotThrow(() -> {
            TagDTO newDto = tagService.createTag(tagDtoToBeCreated);
            assertNotSame(tagDtoToBeCreated, newDto);
            assertEquals(mockedCreatedTag.getId(), newDto.getId());
            assertEquals(mockedCreatedTag.getName(), newDto.getName());
        });
    }

    @Test
    public void createTagShouldThrowExceptionWhenPassedDtoWithNameAlreadyExistingInDatabase() {
        TagRepository tagRepository = Mockito.mock(TagRepository.class);
        Mockito.doThrow(DataIntegrityViolationException.class).when(tagRepository)
                .createTag(Mockito.argThat(arg -> arg.getName().equals(tagDtoToBeCreated.getName())));
        TagService tagService = new TagServiceImpl(tagRepository, tagConverter, pagedResultConverter);
        ReflectionTestUtils.setField(tagService, ALREADY_EXISTS_MESSAGE_FIELD_NAME, MOCK_EX_MESSAGE, String.class);
        InvalidTagException ex = assertThrows(InvalidTagException.class, () -> tagService.createTag(tagDtoToBeCreated));
        assertEquals(InvalidTagException.Reason.ALREADY_EXISTS, ex.getReason());
    }

    @Test
    public void getTagShouldReturnDtoWhenPassedExistingTagId() {
        TagRepository tagRepository = Mockito.mock(TagRepository.class);
        Mockito.when(tagRepository.getTagById(Mockito.eq(existingTagDto1.getId())))
				.thenReturn(Optional.of(existingTag1));
        TagService tagService = new TagServiceImpl(tagRepository, tagConverter, pagedResultConverter);
        assertDoesNotThrow(() -> {
            TagDTO res = tagService.getTag(existingTagDto1.getId());
            assertEquals(existingTagDto1, res);
        });
    }

    @Test
    public void getTagShouldThrowExceptionWhenPassedNonExistingTagId() {
        TagRepository tagRepository = Mockito.mock(TagRepository.class);
        Mockito.when(tagRepository.getTagById(Mockito.eq(nonExistingTagDto.getId()))).thenReturn(Optional.empty());
        TagService tagService = new TagServiceImpl(tagRepository, tagConverter, pagedResultConverter);
        ReflectionTestUtils.setField(tagService, NOT_FOUND_MESSAGE_FIELD_NAME, MOCK_EX_MESSAGE, String.class);
        InvalidTagException ex =
				assertThrows(InvalidTagException.class, () -> tagService.getTag(existingTagDto1.getId()));
        assertEquals(InvalidTagException.Reason.NOT_FOUND, ex.getReason());
    }

    @Test
    public void getTagByNameShouldReturnDtoWhenPassedExistingTagName() {
        TagRepository tagRepository = Mockito.mock(TagRepository.class);
        Mockito.when(tagRepository.getTagByName(Mockito.eq(existingTagDto1.getName())))
				.thenReturn(Optional.of(existingTag1));
        TagService tagService = new TagServiceImpl(tagRepository, tagConverter, pagedResultConverter);
        assertDoesNotThrow(() -> {
            TagDTO res = tagService.getTag(existingTagDto1.getName());
            assertEquals(existingTagDto1, res);
        });
    }

    @Test
    public void getTagByNameShouldThrowExceptionWhenPassedNonExistingTagName() {
        TagRepository tagRepository = Mockito.mock(TagRepository.class);
        Mockito.when(tagRepository.getTagByName(Mockito.eq(nonExistingTagDto.getName()))).thenReturn(Optional.empty());
        TagService tagService = new TagServiceImpl(tagRepository, tagConverter, pagedResultConverter);
        ReflectionTestUtils.setField(tagService, NOT_FOUND_MESSAGE_FIELD_NAME, MOCK_EX_MESSAGE, String.class);
        InvalidTagException ex =
				assertThrows(InvalidTagException.class, () -> tagService.getTag(nonExistingTagDto.getName()));
        assertEquals(InvalidTagException.Reason.NOT_FOUND, ex.getReason());
    }

    @Test
    public void deleteTagShouldNotThrowExceptionWhenPassedExistingId() {
        TagRepository tagRepository = Mockito.mock(TagRepository.class);
        Mockito.when(tagRepository.deleteTag(Mockito.eq(existingTagDto1.getId()))).thenReturn(true);
        TagService tagService = new TagServiceImpl(tagRepository, tagConverter, pagedResultConverter);
        assertDoesNotThrow(() -> tagService.deleteTag(existingTagDto1.getId()));
    }

    @Test
    public void deleteTagShouldThrowExceptionWhenPassedNonExistingId() {
        TagRepository tagRepository = Mockito.mock(TagRepository.class);
        Mockito.when(tagRepository.deleteTag(Mockito.eq(existingTagDto1.getId()))).thenReturn(false);
        TagService tagService = new TagServiceImpl(tagRepository, tagConverter, pagedResultConverter);
        ReflectionTestUtils.setField(tagService, NOT_FOUND_MESSAGE_FIELD_NAME, MOCK_EX_MESSAGE, String.class);
        InvalidTagException ex =
				assertThrows(InvalidTagException.class, () -> tagService.deleteTag(existingTagDto1.getId()));
        assertEquals(InvalidTagException.Reason.NOT_FOUND, ex.getReason());
    }

    @Test
    public void getAllTagsShouldNotThrowExceptionAndReturnList() {
        TagRepository tagRepository = Mockito.mock(TagRepository.class);
        Mockito.when(tagRepository.getAllTags(Mockito.eq(pageDTO.getOffset()), Mockito.eq(pageDTO.getPageSize())))
				.thenReturn(tagPage);
        TagService tagService = new TagServiceImpl(tagRepository, tagConverter, pagedResultConverter);
        assertDoesNotThrow(() -> {
            PagedResultDTO<TagDTO> tagDTOPagedResult = tagService.getAllTags(pageDTO);
            assertEquals(tagPageDto, tagDTOPagedResult);
        });
    }

    @Test
    public void getTagsFromNameSetShouldReturnEmptyTagDtoSetWhenPassedEmptyNameSet() {
        TagRepository tagRepository = Mockito.mock(TagRepository.class);
        TagService tagService = new TagServiceImpl(tagRepository, tagConverter, pagedResultConverter);
        assertDoesNotThrow(() -> {
            Set<TagDTO> set = tagService.getTagsFromNameSet(new HashSet<>());
            assertEquals(EMPTY_SET, set);
        });
    }

    @Test
    public void getTagsFromNameSetShouldReturnTagDtoSetWhenPassedCorrectNameSet() {
        TagRepository tagRepository = Mockito.mock(TagRepository.class);
        TagService tagService = new TagServiceImpl(tagRepository, tagConverter, pagedResultConverter);
        Set tagNameSet = Set.of(existingTagDto1.getName(), nonExistingTagDto.getName());
        Mockito.when(tagRepository.getTagsFromNameSet(Mockito.eq(tagNameSet)))
                .thenReturn(List.of(existingTag1));
        assertDoesNotThrow(() -> {
            Set<TagDTO> set = tagService.getTagsFromNameSet(tagNameSet);
            assertEquals(Set.of(existingTagDto1), set);
        });
    }
}
