package com.epam.esm.service.impl;

import com.epam.esm.db.TagRepository;
import com.epam.esm.model.dto.TagDTO;
import com.epam.esm.model.entity.Tag;
import com.epam.esm.service.TagService;
import com.epam.esm.service.exception.ServiceException;
import com.epam.esm.service.exception.TagAlreadyExistsException;
import com.epam.esm.service.exception.TagNotFoundException;
import com.epam.esm.service.validator.TagValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TagServiceImpl implements TagService {

	private TagRepository tagRepository;
	private TagValidator tagValidator;

	@Autowired
	public TagServiceImpl(TagRepository tagRepository,
	                      TagValidator tagValidator)
	{
		this.tagRepository = tagRepository;
		this.tagValidator = tagValidator;
	}

	@Override
	public void createTag(TagDTO tag) throws ServiceException {
		tagValidator.validateTag(tag);
		try {
			tagRepository.createTag(tag);
		} catch (DuplicateKeyException ex) {
			throw new TagAlreadyExistsException(ex, tag.getName());
		}

	}

	@Override
	public TagDTO getTag(int id) throws ServiceException {
		Optional<Tag> tagOptional = tagRepository.getTagById(id);
		if (tagOptional.isEmpty()) {
			throw new TagNotFoundException(id);
		}
		TagDTO dto = new TagDTO(id, tagOptional.get().getName());
		return dto;
	}

	@Override
	public void deleteTag(int id) throws ServiceException {
		if (!tagRepository.deleteTag(id)) {
			throw new TagNotFoundException(id);
		}
	}

	@Override
	public List<TagDTO> getAllTags() throws ServiceException {
		List<Tag> tagList = tagRepository.getAllTags();
		return tagList.stream().map(t -> new TagDTO(t.getId(), t.getName()))
				.toList();
	}
}
