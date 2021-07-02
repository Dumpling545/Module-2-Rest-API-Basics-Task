package com.epam.esm.service.impl;

import com.epam.esm.db.TagRepository;
import com.epam.esm.model.dto.TagDTO;
import com.epam.esm.model.entity.Tag;
import com.epam.esm.service.TagService;
import com.epam.esm.service.converter.TagConverter;
import com.epam.esm.service.exception.InvalidTagException;
import com.epam.esm.service.exception.ServiceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TagServiceImpl implements TagService {

	private TagRepository tagRepository;
	private TagConverter tagConverter;
	@Value("${tag.exception.already-exists}")
	private String alreadyExistsExceptionTemplate;
	@Value("${tag.exception.not-found}")
	private String notFoundExceptionTemplate;


	public TagServiceImpl(TagRepository tagRepository, TagConverter tagConverter){
		this.tagRepository = tagRepository;
		this.tagConverter = tagConverter;
	}

	@Override
	public TagDTO createTag(TagDTO tagDto) {
		Tag tag = tagConverter.convert(tagDto);
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

	@Override
	public TagDTO getTag(int id) {
		Optional<Tag> tagOptional = Optional.empty();
		try {
			tagOptional = tagRepository.getTagById(id);
		} catch (DataAccessException ex) {
			throw new ServiceException(ex);
		}
		TagDTO tagDTO = tagOptional.map(tagConverter::convert).orElseThrow(() -> {
			String identifier = "id=" + id;
			String message = String.format(notFoundExceptionTemplate, identifier);
			return new InvalidTagException(message, InvalidTagException.Reason.NOT_FOUND, id);
		});
		return tagDTO;
	}

	@Override
	public TagDTO getTag(String tagName) {
		Optional<Tag> tagOptional = Optional.empty();
		try {
			tagOptional = tagRepository.getTagByName(tagName);
		} catch (DataAccessException ex) {
			throw new ServiceException(ex);
		}
		TagDTO tagDTO = tagOptional.map(tagConverter::convert).orElseThrow(() -> {
			String identifier = "name=" + tagName;
			String message = String.format(notFoundExceptionTemplate, identifier);
			return new InvalidTagException(message, InvalidTagException.Reason.NOT_FOUND, tagName);
		});
		return tagDTO;
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
			throw new InvalidTagException(message, InvalidTagException.Reason.NOT_FOUND, id);
		}
	}

	@Override
	public List<TagDTO> getAllTags() {
		List<Tag> tagList = Collections.EMPTY_LIST;
		try {
			tagList = tagRepository.getAllTags();
		} catch (DataAccessException ex) {
			throw new ServiceException(ex);
		}
		List<TagDTO> dtoList = tagList.stream().map(tagConverter::convert).toList();
		return dtoList;
	}

	@Override
	public Set<TagDTO> getTagsFromNameSet(Set<String> tagNames) {
		if (tagNames.size() == 0) {
			return Collections.EMPTY_SET;
		}
		List<Tag> tagList = Collections.EMPTY_LIST;
		try {
			tagList = tagRepository.getTagsFromNameSet(tagNames);
		} catch (DataAccessException ex) {
			throw new ServiceException(ex);
		}
		Set<TagDTO> dtoSet = tagList.stream().map(tagConverter::convert).collect(Collectors.toSet());
		return dtoSet;
	}
}
