package com.epam.esm.service.impl;

import com.epam.esm.db.GiftCertificateRepository;
import com.epam.esm.model.dto.FilterDTO;
import com.epam.esm.model.dto.GiftCertificateCreateDTO;
import com.epam.esm.model.dto.GiftCertificateOutputDTO;
import com.epam.esm.model.dto.GiftCertificateUpdateDTO;
import com.epam.esm.model.dto.TagDTO;
import com.epam.esm.model.entity.Filter;
import com.epam.esm.model.entity.GiftCertificate;
import com.epam.esm.model.entity.Tag;
import com.epam.esm.service.GiftCertificateService;
import com.epam.esm.service.TagService;
import com.epam.esm.service.converter.FilterConverter;
import com.epam.esm.service.converter.GiftCertificateConverter;
import com.epam.esm.service.converter.TagConverter;
import com.epam.esm.service.exception.InvalidCertificateException;
import com.epam.esm.service.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.EMPTY_LIST;


@Service
@RequiredArgsConstructor
public class GiftCertificateServiceImpl implements GiftCertificateService {

	@Value("${cert.exception.not-found}")
	private String notFoundExceptionTemplate;

	private final TagService tagService;
	private final GiftCertificateRepository giftCertificateRepository;
	private final GiftCertificateConverter giftCertificateConverter;
	private final TagConverter tagConverter;
	private final FilterConverter filterConverter;

	private InvalidCertificateException createNotFoundException(int id) {
		String identifier = "id=" + id;
		String message = String.format(notFoundExceptionTemplate, identifier);
		return new InvalidCertificateException(message, InvalidCertificateException.Reason.NOT_FOUND, id);
	}

	private Set<Tag> prepareTagsForCreateUpdate(Set<String> tagNames) {
		if (tagNames == null) {
			return Collections.EMPTY_SET;
		}
		//get tags that are already exists in database
		Set<TagDTO> existingTagDTOs = tagService.getTagsFromNameSet(tagNames);
		Set<String> existingTagNames = existingTagDTOs.stream().map(TagDTO::getName).collect(Collectors.toSet());
		//get tag names that are not in database yet
		Set<String> newTagNames = tagNames.stream().filter(s -> !existingTagNames.contains(s))
				.collect(Collectors.toSet());
		//convert  existing tag dtos to tags
		Set<Tag> existingTags =
				existingTagDTOs.stream().map(tagConverter::convert).collect(Collectors.toSet());
		//convert new tag names to tags
		Set<Tag> newTags = newTagNames.stream().map(tn -> Tag.builder().name(tn).build()).collect(Collectors.toSet());
		Set<Tag> tags = Stream.concat(existingTags.stream(), newTags.stream()).collect(Collectors.toSet());
		return tags;
	}

	@Transactional
	public GiftCertificateOutputDTO createCertificate(GiftCertificateCreateDTO dto) {
		Set<Tag> tags = prepareTagsForCreateUpdate(dto.getTagNames());
		GiftCertificate input = giftCertificateConverter.convert(dto, tags);
		input.setId(null);
		GiftCertificate output;
		try {
			output = giftCertificateRepository.createCertificate(input);
		} catch (DataAccessException ex) {
			throw new ServiceException(ex);
		}
		GiftCertificateOutputDTO outputDTO = giftCertificateConverter.convert(output);
		return outputDTO;
	}

	@Override
	public GiftCertificateOutputDTO getCertificate(int id) {
		Optional<GiftCertificate> optionalCert = Optional.empty();
		try {
			optionalCert = giftCertificateRepository.getCertificateById(id);
		} catch (DataAccessException ex) {
			throw new ServiceException(ex);
		}
		GiftCertificateOutputDTO dto = optionalCert.map(giftCertificateConverter::convert)
				.orElseThrow(() -> createNotFoundException(id));
		return dto;
	}


	@Transactional
	public void updateCertificate(int id, GiftCertificateUpdateDTO dto) {
		try {
			Optional<GiftCertificate> optionalCert = giftCertificateRepository.getCertificateById(id);
			GiftCertificate cert = optionalCert.orElseThrow(() -> createNotFoundException(id));
			Set<Tag> tags = prepareTagsForCreateUpdate(dto.getTagNames());
			GiftCertificate updatedCert = cert.toBuilder().tags(new HashSet<>(cert.getTags())).build();
			giftCertificateConverter.mergeGiftCertificate(updatedCert, dto, tags);
			if (!updatedCert.equals(cert)) {
				giftCertificateRepository.updateCertificate(updatedCert);
			}
		} catch (DataAccessException ex) {
			throw new ServiceException(ex);
		}
	}

	@Override
	public void deleteCertificate(int id) {
		boolean deleted = false;
		try {
			deleted = giftCertificateRepository.deleteCertificate(id);
		} catch (DataAccessException ex) {
			throw new ServiceException(ex);
		}
		if (!deleted) {
			throw createNotFoundException(id);
		}
	}

	@Override
	public List<GiftCertificateOutputDTO> getCertificates(FilterDTO filterDto) {
		Set<Tag> tags = null;
		if (filterDto.getTagNames() != null) {
			Set<TagDTO> tagDTOs = tagService.getTagsFromNameSet(filterDto.getTagNames());
			if (filterDto.getTagNames().size() != tagDTOs.size()) {
				return EMPTY_LIST;
			}
			tags = tagDTOs.stream().map(tagConverter::convert).collect(Collectors.toSet());
		}
		Filter filter = filterConverter.convert(filterDto, tags);
		List<GiftCertificateOutputDTO> outputDTOList = new ArrayList<>();
		try {
			List<GiftCertificate> certs = giftCertificateRepository.getCertificatesByFilter(filter);
			for (GiftCertificate cert : certs) {
				GiftCertificateOutputDTO outputDTO = giftCertificateConverter.convert(cert);
				outputDTOList.add(outputDTO);
			}
		} catch (DataAccessException ex) {
			throw new ServiceException(ex);
		}
		return outputDTOList;
	}
}
