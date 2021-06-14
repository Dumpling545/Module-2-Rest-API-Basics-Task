package com.epam.esm.service.impl;

import com.epam.esm.db.GiftCertificateRepository;
import com.epam.esm.model.dto.*;
import com.epam.esm.model.entity.Filter;
import com.epam.esm.model.entity.GiftCertificate;
import com.epam.esm.model.entity.Tag;
import com.epam.esm.service.GiftCertificateService;
import com.epam.esm.service.TagService;
import com.epam.esm.service.converter.Converter;
import com.epam.esm.service.exception.InvalidCertificateException;
import com.epam.esm.service.exception.ServiceException;
import com.epam.esm.service.merger.Merger;
import com.epam.esm.service.validator.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Service
public class GiftCertificateServiceImpl implements GiftCertificateService {

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

	@Autowired
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


	private List<Tag> createAndAddNonPresentTags(int certificateId, List<String> inputNames) {
		/*
		List<Tag> all = tagRepository.getAllTags();
		List<Tag> existingTags = all.stream().filter(t -> inputNames.contains(t.getName()))
				.collect(Collectors.toCollection(ArrayList::new));
		List<String> existingTagNames = existingTags.stream().map(Tag::getName).toList();
		List<Tag> newTags = inputNames.stream().filter(s -> !existingTagNames.contains(s)).map(Tag::new).toList();
		newTags.forEach(tag -> tagRepository.createTag(tag));
		existingTags.addAll(newTags);
		existingTags.forEach(tag -> giftCertificateRepository.addTag(certificateId, tag.getId()));
		return existingTags;*/
		return null;
	}

	@Transactional
	public GiftCertificateOutputDTO createCertificate(GiftCertificateCreateDTO dto) throws ServiceException {
		/*
		giftCertificateValidator.validateCertificate(dto, false);
		dto.getTags().forEach(s -> tagValidator.validateTagName(s));
		giftCertificateRepository.createCertificate(dto);
		List<Tag> tags = createAndAddNonPresentTags(dto.getId(), dto.getTags());
		List<TagDTO> tagDTOs = tags.stream().map(TagDTO::new).toList();
		GiftCertificateOutputDTO outputDTO = new GiftCertificateOutputDTO(dto, tagDTOs);
		return outputDTO; */
		return null;
	}

	@Override
	public GiftCertificateOutputDTO getCertificate(int id) throws ServiceException {
		Set<TagDTO> tags = tagService.getTagsByCertificate(id);
		Optional<GiftCertificate> optionalCert = Optional.empty();
		try {
			optionalCert = giftCertificateRepository.getCertificateById(id);
		} catch (DataAccessException ex) {
			throw new ServiceException(ex);
		}
		GiftCertificateOutputDTO dto = optionalCert.map(certToOutputDtoConverter::convert).orElseThrow(() -> {
			String identifier = "id=" + id;
			String message = String.format(notFoundExceptionTemplate, identifier);
			return new InvalidCertificateException(message, InvalidCertificateException.Reason.NOT_FOUND);
		});
		dto.getTags().addAll(tags);
		return dto;
	}

	@Transactional
	public GiftCertificateOutputDTO updateCertificate(GiftCertificateUpdateDTO dto) throws ServiceException {
		/*
		Optional<GiftCertificate> optionalGiftCertificate = giftCertificateRepository.getCertificateById(dto.getId());
		if (optionalGiftCertificate.isPresent()) {
			giftCertificateValidator.validateCertificate(dto, true);
			dto.getTags().forEach(s -> tagValidator.validateTagName(s));
			List<Tag> currentTagsOnCertificate = tagRepository.getTagsByCertificate(dto.getId());
			Map<Integer, String> currentTagsOnCertificateMap =
					currentTagsOnCertificate.stream().collect(Collectors.toMap(Tag::getId, Tag::getName));
			List<String> nonPresentTagNames =
					dto.getTags().stream().filter(s -> !currentTagsOnCertificateMap.containsValue(s)).toList();
			List<Tag> addedTags = createAndAddNonPresentTags(dto.getId(), nonPresentTagNames);
			List<Integer> obsoleteTagIds =
					currentTagsOnCertificateMap.entrySet().stream().filter(e -> !dto.getTags().contains(e.getValue()))
							.map(Map.Entry::getKey).toList();
			obsoleteTagIds.forEach(id -> giftCertificateRepository.removeTag(dto.getId(), id));
			GiftCertificate giftCertificate = optionalGiftCertificate.get();
			merge(giftCertificate, dto);
			giftCertificateRepository.updateCertificate(giftCertificate);
			List<TagDTO> currentTagDTOs =
					currentTagsOnCertificate.stream().map(TagDTO::new).collect(Collectors.toCollection(ArrayList::new));
			List<TagDTO> addedTagDTOs = addedTags.stream().map(TagDTO::new).toList();
			currentTagDTOs.addAll(addedTagDTOs);
			currentTagDTOs.removeIf(t -> obsoleteTagIds.contains(t.getId()));
			GiftCertificateOutputDTO outputDTO = new GiftCertificateOutputDTO(giftCertificate, currentTagDTOs);
			return outputDTO;
		} else {
			throw new GiftCertificateNotFoundException(dto.getId());
		}
		 */
		return null;
	}

	@Override
	public void deleteCertificate(int id) throws ServiceException {
		boolean deleted = false;
		try {
			deleted = giftCertificateRepository.deleteCertificate(id);
		} catch (DataAccessException ex) {
			throw new ServiceException(ex);
		}
		if (!deleted) {
			String identifier = "id=" + id;
			String message = String.format(notFoundExceptionTemplate, identifier);
			throw new InvalidCertificateException(message, InvalidCertificateException.Reason.NOT_FOUND);
		}
	}

	@Override
	public List<GiftCertificateOutputDTO> getCertificates(FilterDTO filterDto) throws ServiceException {
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
