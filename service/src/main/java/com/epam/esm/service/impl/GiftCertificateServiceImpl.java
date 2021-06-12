package com.epam.esm.service.impl;

import com.epam.esm.db.GiftCertificateRepository;
import com.epam.esm.db.TagRepository;
import com.epam.esm.model.dto.FilterDTO;
import com.epam.esm.model.dto.GiftCertificateDTO;
import com.epam.esm.model.dto.GiftCertificateOutputDTO;
import com.epam.esm.model.dto.TagDTO;
import com.epam.esm.model.entity.GiftCertificate;
import com.epam.esm.model.entity.Tag;
import com.epam.esm.service.GiftCertificateService;
import com.epam.esm.service.exception.GiftCertificateNotFoundException;
import com.epam.esm.service.exception.ServiceException;
import com.epam.esm.service.validator.GiftCertificateValidator;
import com.epam.esm.service.validator.TagValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.epam.esm.model.entity.Filter.NO_TAG_SPECIFIED;

@Service
public class GiftCertificateServiceImpl implements GiftCertificateService {

	private TagRepository tagRepository;
	private GiftCertificateRepository giftCertificateRepository;
	private TagValidator tagValidator;
	private GiftCertificateValidator giftCertificateValidator;
	private final static double epsilon = 0.000001d;

	@Autowired
	public GiftCertificateServiceImpl(TagRepository tagRepository, GiftCertificateRepository giftCertificateRepository,
	                                  TagValidator tagValidator, GiftCertificateValidator giftCertificateValidator) {
		this.tagRepository = tagRepository;
		this.giftCertificateRepository = giftCertificateRepository;
		this.tagValidator = tagValidator;
		this.giftCertificateValidator = giftCertificateValidator;
	}

	private List<Tag> createAndAddNonPresentTags(int certificateId, List<String> inputNames) {
		List<Tag> all = tagRepository.getAllTags();
		List<Tag> existingTags = all.stream().filter(t -> inputNames.contains(t.getName()))
				.collect(Collectors.toCollection(ArrayList::new));
		List<String> existingTagNames = existingTags.stream().map(Tag::getName).toList();
		List<Tag> newTags = inputNames.stream().filter(s -> !existingTagNames.contains(s)).map(Tag::new).toList();
		newTags.forEach(tag -> tagRepository.createTag(tag));
		existingTags.addAll(newTags);
		existingTags.forEach(tag -> giftCertificateRepository.addTag(certificateId, tag.getId()));
		return existingTags;
	}

	@Transactional(isolation = Isolation.SERIALIZABLE)
	public GiftCertificateOutputDTO createCertificate(GiftCertificateDTO dto) throws ServiceException {
		giftCertificateValidator.validateCertificate(dto, false);
		dto.getTags().forEach(s -> tagValidator.validateTagName(s));
		giftCertificateRepository.createCertificate(dto);
		List<Tag> tags = createAndAddNonPresentTags(dto.getId(), dto.getTags());
		List<TagDTO> tagDTOs = tags.stream().map(TagDTO::new).toList();
		GiftCertificateOutputDTO outputDTO = new GiftCertificateOutputDTO(dto, tagDTOs);
		return outputDTO;
	}

	@Override
	public GiftCertificateOutputDTO getCertificate(int id) throws ServiceException {
		List<TagDTO> tags = tagRepository.getTagsByCertificate(id).stream().map(TagDTO::new).toList();
		Optional<GiftCertificate> optional = giftCertificateRepository.getCertificateById(id);
		if (optional.isEmpty()) {
			throw new GiftCertificateNotFoundException(id);
		}
		GiftCertificateOutputDTO dto = new GiftCertificateOutputDTO(optional.get(), tags);
		return dto;
	}

	private void merge(GiftCertificate base, GiftCertificate merged) {
		if (merged.getName() != null) {
			base.setName(merged.getName());
		}
		if (merged.getDescription() != null) {
			base.setDescription(merged.getDescription());
		}
		if (merged.getDuration() != GiftCertificateDTO.DEFAULT_DURATION) {
			base.setDuration(merged.getDuration());
		}
		if (Math.abs(merged.getPrice() - GiftCertificateDTO.DEFAULT_PRICE) > epsilon) {
			base.setPrice(merged.getPrice());
		}
	}

	@Transactional(isolation = Isolation.SERIALIZABLE)
	public GiftCertificateOutputDTO updateCertificate(GiftCertificateDTO dto) throws ServiceException {
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
	}

	@Override
	public void deleteCertificate(int id) throws ServiceException {
		if (!giftCertificateRepository.deleteCertificate(id)) {
			throw new GiftCertificateNotFoundException(id);
		}
	}

	@Override
	public List<GiftCertificateOutputDTO> getCertificates(FilterDTO filter) throws ServiceException {
		if (filter.getTagName() != null) {
			tagValidator.validateTagName(filter.getTagName());
			Optional<Tag> tagOptional = tagRepository.getTagByName(filter.getTagName());
			if (tagOptional.isEmpty()) {
				return Collections.EMPTY_LIST;
			}
			filter.setTagId(tagOptional.get().getId());
		} else {
			filter.setTagId(NO_TAG_SPECIFIED);
		}
		List<GiftCertificate> certs = giftCertificateRepository.getCertificatesByFilter(filter);
		List<GiftCertificateOutputDTO> outputDTOList = new ArrayList<>();
		for (GiftCertificate c : certs) {
			List<TagDTO> tags = tagRepository.getTagsByCertificate(c.getId()).stream().map(TagDTO::new).toList();
			outputDTOList.add(new GiftCertificateOutputDTO(c, tags));
		}
		return outputDTOList;
	}
}
