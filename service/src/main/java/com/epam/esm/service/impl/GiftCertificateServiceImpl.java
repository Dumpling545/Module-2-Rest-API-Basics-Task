package com.epam.esm.service.impl;

import com.epam.esm.db.GiftCertificateRepository;
import com.epam.esm.model.dto.GiftCertificateCreateDTO;
import com.epam.esm.model.dto.GiftCertificateOutputDTO;
import com.epam.esm.model.dto.GiftCertificateSearchFilterDTO;
import com.epam.esm.model.dto.GiftCertificateUpdateDTO;
import com.epam.esm.model.dto.TagDTO;
import com.epam.esm.model.entity.GiftCertificate;
import com.epam.esm.model.entity.GiftCertificateSearchFilter;
import com.epam.esm.model.entity.Tag;
import com.epam.esm.service.GiftCertificateService;
import com.epam.esm.service.TagService;
import com.epam.esm.service.converter.GiftCertificateConverter;
import com.epam.esm.service.converter.GiftCertificateSearchFilterConverter;
import com.epam.esm.service.converter.TagConverter;
import com.epam.esm.service.exception.InvalidCertificateException;
import com.epam.esm.service.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;


@Service
@RequiredArgsConstructor
public class GiftCertificateServiceImpl implements GiftCertificateService {

	private final TagService tagService;
	private final GiftCertificateRepository giftCertificateRepository;
	private final GiftCertificateConverter giftCertificateConverter;
	private final TagConverter tagConverter;
	private final GiftCertificateSearchFilterConverter giftCertificateSearchFilterConverter;
	@Value("${cert.exception.not-found}")
	private String notFoundExceptionTemplate;
	@Value("${cert.exception.sort-by.invalid-field}")
	private String invalidFieldTokenTemplate;

	private InvalidCertificateException createNotFoundException(int id, Exception cause) {
		String identifier = "id=" + id;
		String message = String.format(notFoundExceptionTemplate, identifier);
		return new InvalidCertificateException(message, cause, InvalidCertificateException.Reason.NOT_FOUND, id);
	}

	private Set<Tag> prepareTagsForCreateUpdate(Set<String> tagNames) {
		if (tagNames == null) {
			return Collections.EMPTY_SET;
		}
		//get tags that are already exists in database
		Set<TagDTO> existingTagDTOs = tagService.getTagsFromNameSet(tagNames);
		Set<String> existingTagNames = existingTagDTOs.stream().map(TagDTO::getName).collect(toSet());
		//get tag names that are not in database yet
		Set<String> newTagNames = tagNames.stream().filter(s -> !existingTagNames.contains(s))
				.collect(toSet());
		//convert existing tag dtos to tags
		Set<Tag> existingTags = existingTagDTOs.stream().map(tagConverter::convert).collect(toSet());
		//convert new tag names to tag DTO's
		Set<TagDTO> newTagDTOs = newTagNames.stream().map(tn -> TagDTO.builder().name(tn).build()).collect(toSet());
		//persist new tags
		Set<TagDTO> persistedNewTagDTOs = tagService.createTags(newTagDTOs);
		//convert newly persisted tag dtos to tags
		Set<Tag> persistedNewTags = persistedNewTagDTOs.stream().map(tagConverter::convert).collect(toSet());
		return Stream.concat(existingTags.stream(), persistedNewTags.stream()).collect(toSet());
	}

	@Transactional
	public GiftCertificateOutputDTO createCertificate(GiftCertificateCreateDTO dto) {
		Set<Tag> tags = prepareTagsForCreateUpdate(dto.getTagNames());
		GiftCertificate input = giftCertificateConverter.convert(dto, tags);
		input.setId(null);
		GiftCertificate output;
		try {
			output = giftCertificateRepository.save(input);
		} catch (DataAccessException ex) {
			throw new ServiceException(ex);
		}
		return giftCertificateConverter.convert(output);
	}

	@Override
	public GiftCertificateOutputDTO getCertificate(int id) {
		Optional<GiftCertificate> optionalCert;
		try {
			optionalCert = giftCertificateRepository.findById(id);
		} catch (DataAccessException ex) {
			throw new ServiceException(ex);
		}
		return optionalCert.map(giftCertificateConverter::convert)
				.orElseThrow(() -> createNotFoundException(id, null));
	}


	@Transactional
	public void updateCertificate(int id, GiftCertificateUpdateDTO dto) {
		try {
			Optional<GiftCertificate> optionalCert = giftCertificateRepository.findById(id);
			GiftCertificate cert = optionalCert.orElseThrow(() -> createNotFoundException(id, null));
			Set<Tag> tags = prepareTagsForCreateUpdate(dto.getTagNames());
			GiftCertificate updatedCert = cert.toBuilder().tags(new HashSet<>(cert.getTags())).build();
			giftCertificateConverter.mergeGiftCertificate(updatedCert, dto, tags);
			if (!updatedCert.equals(cert)) {
				giftCertificateRepository.save(updatedCert);
			}
		} catch (DataAccessException ex) {
			throw new ServiceException(ex);
		}
	}

	@Override
	public void deleteCertificate(int id) {
		try {
			giftCertificateRepository.deleteById(id);
		} catch (EmptyResultDataAccessException ex) {
			throw createNotFoundException(id, ex);
		} catch (DataAccessException ex) {
			throw new ServiceException(ex);
		}
	}

	@Override
	public Slice<GiftCertificateOutputDTO> getCertificates(
			GiftCertificateSearchFilterDTO giftCertificateSearchFilterDTO,
			Pageable pageable) {
		GiftCertificateSearchFilter giftCertificateSearchFilter = giftCertificateSearchFilterConverter.convert(
				giftCertificateSearchFilterDTO);
		Slice<GiftCertificate> slice;
		try {
			slice = giftCertificateRepository
					.getCertificatesByFilter(giftCertificateSearchFilter, pageable);
		} catch (PropertyReferenceException ex) {
			String message = String.format(invalidFieldTokenTemplate, ex.getPropertyName());
			throw new InvalidCertificateException(message, ex, InvalidCertificateException.Reason.INVALID_SORT_BY);
		} catch (DataAccessException ex) {
			throw new ServiceException(ex);
		}
		return slice.map(giftCertificateConverter::convert);
	}
}
