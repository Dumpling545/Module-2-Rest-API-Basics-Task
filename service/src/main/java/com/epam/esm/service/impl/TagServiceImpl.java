package com.epam.esm.service.impl;

import com.epam.esm.db.TagRepository;
import com.epam.esm.model.dto.PageDTO;
import com.epam.esm.model.dto.PagedResultDTO;
import com.epam.esm.model.dto.TagDTO;
import com.epam.esm.model.entity.PagedResult;
import com.epam.esm.model.entity.Tag;
import com.epam.esm.service.TagService;
import com.epam.esm.service.converter.PagedResultConverter;
import com.epam.esm.service.converter.TagConverter;
import com.epam.esm.service.exception.InvalidTagException;
import com.epam.esm.service.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

	private final TagRepository tagRepository;
	private final TagConverter tagConverter;
	private final PagedResultConverter pagedResultConverter;
	@Value("${tag.exception.already-exists}")
	private String alreadyExistsExceptionTemplate;
	@Value("${tag.exception.not-found}")
	private String notFoundExceptionTemplate;

	@Override
	public TagDTO createTag(TagDTO tagDto) {
		Tag tag = tagConverter.convert(tagDto);
		tag.setId(null);
		try {
			Tag newTag = tagRepository.createTag(tag);
			return tagConverter.convert(newTag);
		} catch (DataIntegrityViolationException ex) {
			String message = String.format(alreadyExistsExceptionTemplate, tagDto.getName());
			throw new InvalidTagException(message, ex, InvalidTagException.Reason.ALREADY_EXISTS, tagDto.getName());
		} catch (DataAccessException ex) {
			throw new ServiceException(ex);
		}
	}

	private TagDTO getSingleTag(Supplier<Optional<Tag>> tagOptionalSupplier, Supplier<String> tagDescriptionSupplier) {
		Optional<Tag> tagOptional;
		try {
			tagOptional = tagOptionalSupplier.get();
		} catch (DataAccessException ex) {
			throw new ServiceException(ex);
		}
		return tagOptional.map(tagConverter::convert).orElseThrow(() -> {
			String identifier = tagDescriptionSupplier.get();
			String message = String.format(notFoundExceptionTemplate, identifier);
			return new InvalidTagException(message, InvalidTagException.Reason.NOT_FOUND, identifier);
		});
	}

	@Override
	public TagDTO getTag(int id) {
		return getSingleTag(() -> tagRepository.getTagById(id), () -> "id=" + id);
	}

	@Override
	public TagDTO getTag(String tagName) {
		return getSingleTag(() -> tagRepository.getTagByName(tagName), () -> "name=" + tagName);
	}

	@Override
	public TagDTO getMostWidelyUsedTagOfUserWithHighestCostOfAllOrders(int userId) {
		return getSingleTag(() -> tagRepository.getMostWidelyUsedTagOfUserWithHighestCostOfAllOrders(userId),
		                    () -> "userId=" + userId);
	}

	@Override
	public void deleteTag(int id) {
		boolean deleted = false;
		try {
			deleted = tagRepository.deleteTag(id);
		} catch (DataAccessException ex) {
			throw new ServiceException(ex);
		}
		if (!deleted) {
			String identifier = "id=" + id;
			String message = String.format(notFoundExceptionTemplate, identifier);
			throw new InvalidTagException(message, InvalidTagException.Reason.NOT_FOUND, identifier);
		}
	}

	@Override
	public PagedResultDTO<TagDTO> getAllTags(PageDTO pageDTO) {
		PagedResult<Tag> pagedResult;
		try {
			pagedResult = tagRepository.getAllTags(pageDTO.getOffset(), pageDTO.getPageSize());
		} catch (DataAccessException ex) {
			throw new ServiceException(ex);
		}
		return pagedResultConverter.convertToTagPage(pagedResult);
	}

	@Override
	public Set<TagDTO> getTagsFromNameSet(Set<String> tagNames) {
		if (tagNames.size() == 0) {
			return Collections.EMPTY_SET;
		}
		List<Tag> tagList;
		try {
			tagList = tagRepository.getTagsFromNameSet(tagNames);
		} catch (DataAccessException ex) {
			throw new ServiceException(ex);
		}
		return tagList.stream().map(tagConverter::convert).collect(Collectors.toSet());
	}
}
