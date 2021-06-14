package com.epam.esm.service.impl;

import com.epam.esm.db.TagRepository;
import com.epam.esm.model.dto.TagDTO;
import com.epam.esm.model.entity.Tag;
import com.epam.esm.service.TagService;
import com.epam.esm.service.converter.Converter;
import com.epam.esm.service.exception.InvalidTagException;
import com.epam.esm.service.exception.ServiceException;
import com.epam.esm.service.validator.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TagServiceImpl implements TagService {

	private TagRepository tagRepository;
	private Validator<TagDTO> tagValidator;
	private Converter<TagDTO, Tag> tagDtoToTagConverter;
	private Converter<Tag, TagDTO> tagToTagDtoConverter;

	@Value("${tag.exception.already-exists}")
	private String alreadyExistsExceptionTemplate;
	@Value("${tag.exception.not-found}")
	private String notFoundExceptionTemplate;

	@Autowired
	public TagServiceImpl(TagRepository tagRepository, Validator<TagDTO> tagValidator,
	                      Converter<TagDTO, Tag> tagDtoToTagConverter, Converter<Tag, TagDTO> tagToTagDtoConverter) {
		this.tagRepository = tagRepository;
		this.tagValidator = tagValidator;
		this.tagDtoToTagConverter = tagDtoToTagConverter;
		this.tagToTagDtoConverter = tagToTagDtoConverter;
	}

	@Override
	public TagDTO createTag(TagDTO tagDto) throws ServiceException {
		tagValidator.validate(tagDto);
		Tag tag = tagDtoToTagConverter.convert(tagDto);
		Tag newTag = null;
		try {
			newTag = tagRepository.createTag(tag);
		} catch (DuplicateKeyException ex) {
			String message = String.format(alreadyExistsExceptionTemplate, tagDto.getName());
			throw new InvalidTagException(message, ex, InvalidTagException.Reason.ALREADY_EXISTS);
		}
		return tagToTagDtoConverter.convert(newTag);

	}

	@Override
	public TagDTO getTag(int id) throws ServiceException {
		Optional<Tag> tagOptional = tagRepository.getTagById(id);
		Tag tag = tagOptional.orElseThrow(() -> {
			String identifier = "id=" + id;
			String message = String.format(notFoundExceptionTemplate, identifier);
			return new InvalidTagException(message, InvalidTagException.Reason.NOT_FOUND);
		});
		return tagToTagDtoConverter.convert(tag);
	}

	@Override
	public void deleteTag(int id) throws ServiceException {
		if (!tagRepository.deleteTag(id)) {
			String identifier = "id=" + id;
			String message = String.format(notFoundExceptionTemplate, identifier);
			throw new InvalidTagException(message, InvalidTagException.Reason.NOT_FOUND);
		}
	}

	@Override
	public List<TagDTO> getAllTags() throws ServiceException {
		List<Tag> tagList = tagRepository.getAllTags();
		List<TagDTO> dtoList = tagList.stream().map(tagToTagDtoConverter::convert).toList();
		return dtoList;
	}
}
