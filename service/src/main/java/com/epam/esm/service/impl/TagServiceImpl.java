package com.epam.esm.service.impl;

import com.epam.esm.db.TagRepository;
import com.epam.esm.model.dto.TagDTO;
import com.epam.esm.model.entity.Tag;
import com.epam.esm.service.TagService;
import com.epam.esm.service.converter.Converter;
import com.epam.esm.service.exception.InvalidTagException;
import com.epam.esm.service.exception.ServiceException;
import com.epam.esm.service.validator.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TagServiceImpl implements TagService {

	private TagRepository tagRepository;
	private Validator<TagDTO> tagValidator;
	private Converter<TagDTO, Tag> tagDtoToTagConverter;
	private Converter<Tag, TagDTO> tagToTagDtoConverter;

	private static final int DEFAULT_TAG_ID = -1;

	@Value("${tag.exception.already-exists}")
	private String alreadyExistsExceptionTemplate;
	@Value("${tag.exception.not-found}")
	private String notFoundExceptionTemplate;

	public TagServiceImpl(TagRepository tagRepository, Validator<TagDTO> tagValidator,
	                      Converter<TagDTO, Tag> tagDtoToTagConverter, Converter<Tag, TagDTO> tagToTagDtoConverter) {
		this.tagRepository = tagRepository;
		this.tagValidator = tagValidator;
		this.tagDtoToTagConverter = tagDtoToTagConverter;
		this.tagToTagDtoConverter = tagToTagDtoConverter;
	}

	@Override
	public TagDTO createTag(TagDTO tagDto) {
		tagValidator.validate(tagDto);
		Tag tag = tagDtoToTagConverter.convert(tagDto);
		Tag newTag = null;
		try {
			newTag = tagRepository.createTag(tag);
		} catch (DuplicateKeyException ex) {
			String message = String.format(alreadyExistsExceptionTemplate, tagDto.getName());
			throw new InvalidTagException(message, ex, InvalidTagException.Reason.ALREADY_EXISTS, tagDto.getName());
		} catch (DataAccessException ex) {
			throw new ServiceException(ex);
		}
		return tagToTagDtoConverter.convert(newTag);

	}

	@Override
	public TagDTO getTag(int id) {
		Optional<Tag> tagOptional = Optional.empty();
		try {
			tagOptional = tagRepository.getTagById(id);
		} catch (DataAccessException ex) {
			throw new ServiceException(ex);
		}
		TagDTO tagDTO = tagOptional.map(tagToTagDtoConverter::convert).orElseThrow(() -> {
			String identifier = "id=" + id;
			String message = String.format(notFoundExceptionTemplate, identifier);
			return new InvalidTagException(message, InvalidTagException.Reason.NOT_FOUND, id);
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
		List<TagDTO> dtoList = tagList.stream().map(tagToTagDtoConverter::convert).toList();
		return dtoList;
	}

	@Override
	public Set<TagDTO> getTagsFromNameSet(Set<String> tagNames) {
		if(tagNames.size() == 0){
			return Collections.EMPTY_SET;
		}
		tagNames.forEach(tn -> tagValidator.validate(new TagDTO(DEFAULT_TAG_ID, tn)));
		List<Tag> tagList = Collections.EMPTY_LIST;
		try {
			tagList = tagRepository.getTagsFromNameSet(tagNames);
		} catch (DataAccessException ex) {
			throw new ServiceException(ex);
		}
		Set<TagDTO> dtoSet = tagList.stream().map(tagToTagDtoConverter::convert).collect(Collectors.toSet());
		return dtoSet;
	}

	@Override
	public Set<TagDTO> getTagsByCertificate(int certificateId) {
		List<Tag> tagList = Collections.EMPTY_LIST;
		try {
			tagList = tagRepository.getTagsByCertificate(certificateId);
		} catch (DataAccessException ex) {
			throw new ServiceException(ex);
		}
		Set<TagDTO> dtoSet = tagList.stream().map(tagToTagDtoConverter::convert).collect(Collectors.toSet());
		return dtoSet;
	}
}
