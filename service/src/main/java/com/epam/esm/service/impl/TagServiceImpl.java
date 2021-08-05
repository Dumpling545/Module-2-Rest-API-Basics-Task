package com.epam.esm.service.impl;

import com.epam.esm.db.TagRepository;
import com.epam.esm.model.dto.TagDTO;
import com.epam.esm.model.entity.Tag;
import com.epam.esm.service.TagService;
import com.epam.esm.service.converter.TagConverter;
import com.epam.esm.service.exception.InvalidTagException;
import com.epam.esm.service.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

	private final TagRepository tagRepository;
	private final TagConverter tagConverter;
	@Value("${tag.exception.already-exists}")
	private String alreadyExistsExceptionTemplate;
	@Value("${tag.exception.not-found}")
	private String notFoundExceptionTemplate;
	@Value("${tag.exception.sort-by.invalid-field}")
	private String invalidFieldTokenTemplate;

	@Override
	public TagDTO createTag(TagDTO tagDto) {
		Tag tag = tagConverter.convert(tagDto);
		tag.setId(null);
		try {
			Tag newTag = tagRepository.save(tag);
			return tagConverter.convert(newTag);
		} catch (DataIntegrityViolationException ex) {
			String message = String.format(alreadyExistsExceptionTemplate, tagDto.getName());
			throw new InvalidTagException(message, ex, InvalidTagException.Reason.ALREADY_EXISTS, tagDto.getName());
		} catch (DataAccessException ex) {
			throw new ServiceException(ex);
		}
	}

	@Override
	public Set<TagDTO> createTags(Set<TagDTO> tagDtos) {
		Set<Tag> tags = tagDtos.stream()
				.map(tagConverter::convert)
				.peek(t -> t.setId(null))
				.collect(toSet());
		try {
			Set<Tag> newTags = tagRepository.saveAll(tags);
			return newTags.stream().map(tagConverter::convert).collect(toSet());
		} catch (DataIntegrityViolationException ex) {
			String names = tags.stream()
					.map(Tag::getName)
					.collect(Collectors.joining(",", "{", "}"));
			String message = String.format(alreadyExistsExceptionTemplate, names);
			throw new InvalidTagException(message, ex, InvalidTagException.Reason.ALREADY_EXISTS, names);
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
		return getSingleTag(() -> tagRepository.findById(id), () -> "id=" + id);
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
		try {
			tagRepository.deleteById(id);
		} catch (EmptyResultDataAccessException ex) {
			String identifier = "id=" + id;
			String message = String.format(notFoundExceptionTemplate, identifier);
			throw new InvalidTagException(message, ex, InvalidTagException.Reason.NOT_FOUND, identifier);
		} catch (DataAccessException ex) {
			throw new ServiceException(ex);
		}
	}

	@Override
	public Slice<TagDTO> getAllTags(Pageable pageable) {
		Slice<Tag> slice;
		try {
			slice = tagRepository.getAllTagsBy(pageable);
		} catch (PropertyReferenceException ex) {
			String message = String.format(invalidFieldTokenTemplate, ex.getPropertyName());
			throw new InvalidTagException(message, ex, InvalidTagException.Reason.INVALID_SORT_BY);
		} catch (DataAccessException ex) {
			throw new ServiceException(ex);
		}
		return slice.map(tagConverter::convert);
	}

	@Override
	public Set<TagDTO> getTagsFromNameSet(Set<String> tagNames) {
		if (tagNames.size() == 0) {
			return Collections.EMPTY_SET;
		}
		List<Tag> tagList;
		try {
			tagList = tagRepository.getTagsByNameIn(tagNames);
		} catch (DataAccessException ex) {
			throw new ServiceException(ex);
		}
		return tagList.stream().map(tagConverter::convert).collect(Collectors.toSet());
	}
}
