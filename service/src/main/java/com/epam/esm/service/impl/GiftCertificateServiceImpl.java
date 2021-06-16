package com.epam.esm.service.impl;

import com.epam.esm.db.GiftCertificateRepository;
import com.epam.esm.model.dto.*;
import com.epam.esm.model.entity.Filter;
import com.epam.esm.model.entity.GiftCertificate;
import com.epam.esm.service.GiftCertificateService;
import com.epam.esm.service.TagService;
import com.epam.esm.service.converter.Converter;
import com.epam.esm.service.exception.InvalidCertificateException;
import com.epam.esm.service.exception.ServiceException;
import com.epam.esm.service.merger.Merger;
import com.epam.esm.service.validator.Validator;
import org.apache.commons.collections4.SetUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;


@Service
public class GiftCertificateServiceImpl implements GiftCertificateService {

	private static final int DEFAULT_ID = -1;
	private TagService tagService;
	private GiftCertificateRepository giftCertificateRepository;
	private Validator<GiftCertificateCreateDTO> certCreateValidator;
	private Validator<GiftCertificateUpdateDTO> certUpdateValidator;
	private Merger<GiftCertificate, GiftCertificateUpdateDTO> updateDtoIntoCertMerger;
	private Converter<FilterDTO, Filter> dtoToFilterConverter;
	private Converter<GiftCertificateCreateDTO, GiftCertificate> createDtoToCertConverter;
	private Converter<GiftCertificate, GiftCertificateOutputDTO> certToOutputDtoConverter;
	@Value("${cert.exception.not-found}")
	private String notFoundExceptionTemplate;

	public GiftCertificateServiceImpl(TagService tagService, GiftCertificateRepository giftCertificateRepository,
	                                  Validator<GiftCertificateCreateDTO> certCreateValidator,
	                                  Validator<GiftCertificateUpdateDTO> certUpdateValidator,
	                                  Merger<GiftCertificate, GiftCertificateUpdateDTO> updateDtoIntoCertMerger,
	                                  Converter<FilterDTO, Filter> dtoToFilterConverter,
	                                  Converter<GiftCertificateCreateDTO, GiftCertificate> createDtoToCertConverter,
	                                  Converter<GiftCertificate, GiftCertificateOutputDTO> certToOutputDtoConverter) {
		this.tagService = tagService;
		this.giftCertificateRepository = giftCertificateRepository;
		this.certCreateValidator = certCreateValidator;
		this.certUpdateValidator = certUpdateValidator;
		this.updateDtoIntoCertMerger = updateDtoIntoCertMerger;
		this.dtoToFilterConverter = dtoToFilterConverter;
		this.createDtoToCertConverter = createDtoToCertConverter;
		this.certToOutputDtoConverter = certToOutputDtoConverter;
	}

	private Set<TagDTO> createTagsFromNameSet(Set<String> tagNames) {
		Set<TagDTO> newlyCreatedTagDTOs = new HashSet<>();
		for (String tagName : tagNames) {
			TagDTO input = new TagDTO(DEFAULT_ID, tagName);
			TagDTO output = tagService.createTag(input);
			newlyCreatedTagDTOs.add(output);
		}
		return newlyCreatedTagDTOs;
	}

	private InvalidCertificateException createNotFoundException(int id) {
		String identifier = "id=" + id;
		String message = String.format(notFoundExceptionTemplate, identifier);
		return new InvalidCertificateException(message, InvalidCertificateException.Reason.NOT_FOUND, id);
	}

	private GiftCertificateOutputDTO createAndAddTagsToCertificate(Set<String> tagNames,
	                                                               Supplier<GiftCertificate> certSupplier) {
		//get tags that are already exists in database
		Set<TagDTO> existingTagDTOs = tagService.getTagsFromNameSet(tagNames);
		Set<String> existingTagNames = existingTagDTOs.stream().map(t -> t.getName()).collect(Collectors.toSet());
		//get tag names that are not in database yet
		Set<String> newTagNames = SetUtils.difference(tagNames, existingTagNames);
		//create new tags and form Set from them
		Set<TagDTO> newlyCreatedTagDTOs = createTagsFromNameSet(newTagNames);
		//create Set from both sets
		Set<TagDTO> tagDTOs = SetUtils.union(existingTagDTOs, newlyCreatedTagDTOs);
		//convert certificate to entity & create it
		GiftCertificate cert = certSupplier.get();
		//add tags to certificate
		for (TagDTO tagDTO : tagDTOs) {
			giftCertificateRepository.addTag(cert.getId(), tagDTO.getId());
		}
		//convert certificate entity to dto
		GiftCertificateOutputDTO outputDTO = certToOutputDtoConverter.convert(cert);
		//add tagDTOs
		outputDTO.getTags().addAll(tagDTOs);
		return outputDTO;
	}

	@Transactional
	public GiftCertificateOutputDTO createCertificate(GiftCertificateCreateDTO dto) {
		certCreateValidator.validate(dto);
		GiftCertificateOutputDTO outputDTO;
		try {
			GiftCertificate input = createDtoToCertConverter.convert(dto);
			//Create and Add certificates, provided certificate creator
			outputDTO = createAndAddTagsToCertificate(dto.getTagNames(),
					() -> giftCertificateRepository.createCertificate(input));
		} catch (DataAccessException ex) {
			throw new ServiceException(ex);
		}
		return outputDTO;
	}

	@Override
	public GiftCertificateOutputDTO getCertificate(int id) {
		Set<TagDTO> tags = tagService.getTagsByCertificate(id);
		Optional<GiftCertificate> optionalCert = Optional.empty();
		try {
			optionalCert = giftCertificateRepository.getCertificateById(id);
		} catch (DataAccessException ex) {
			throw new ServiceException(ex);
		}
		GiftCertificateOutputDTO dto =
				optionalCert.map(certToOutputDtoConverter::convert).orElseThrow(() -> createNotFoundException(id));
		dto.getTags().addAll(tags);
		return dto;
	}

	@Transactional
	public void updateCertificate(int id, GiftCertificateUpdateDTO dto) {
		certUpdateValidator.validate(dto);
		try {
			//retrieve existing certificate
			Optional<GiftCertificate> optionalCert = giftCertificateRepository.getCertificateById(id);
			//throw exception, if certificate not exists
			GiftCertificate cert = optionalCert.orElseThrow(() -> createNotFoundException(id));
			//get all tags, currently associated to provided certificate
			Set<TagDTO> currentTagsOfCertificate = tagService.getTagsByCertificate(cert.getId());
			//collect map from previous set with tag names as keys, and tag ids as values
			Map<String, Integer> mapCurrentTagNamesToIds =
					currentTagsOfCertificate.stream().collect(Collectors.toMap(t -> t.getName(), t -> t.getId()));
			//get tag names needed to be added to certificate
			Set<String> tagNamesToAdd = SetUtils.difference(dto.getTagNames(), mapCurrentTagNamesToIds.keySet());
			//add tags
			createAndAddTagsToCertificate(tagNamesToAdd, () -> cert);
			//get tags names needed to be removed from certificate
			Set<String> tagNamesToRemove = SetUtils.difference(mapCurrentTagNamesToIds.keySet(), dto.getTagNames());
			//remove tags
			for (String tagName : tagNamesToRemove) {
				int tagId = mapCurrentTagNamesToIds.get(tagName);
				giftCertificateRepository.removeTag(cert.getId(), tagId);
			}
			//merge updated dto and current certificate into new certificate
			GiftCertificate updatedCert = updateDtoIntoCertMerger.merge(cert, dto);
			//update only if there are any difference between old and new version
			//if not updated then cert is not presented in database --> throw exception
			if (!updatedCert.equals(cert) && !giftCertificateRepository.updateCertificate(updatedCert)) {
				throw createNotFoundException(cert.getId());
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
		Filter filter = dtoToFilterConverter.convert(filterDto);
		List<GiftCertificateOutputDTO> outputDTOList = new ArrayList<>();
		try {
			List<GiftCertificate> certs = giftCertificateRepository.getCertificatesByFilter(filter);
			for (GiftCertificate cert : certs) {
				Set<TagDTO> tags = tagService.getTagsByCertificate(cert.getId());
				GiftCertificateOutputDTO outputDTO = certToOutputDtoConverter.convert(cert);
				outputDTO.getTags().addAll(tags);
				outputDTOList.add(outputDTO);
			}
		} catch (DataAccessException ex) {
			throw new ServiceException(ex);
		}
		return outputDTOList;
	}
}
