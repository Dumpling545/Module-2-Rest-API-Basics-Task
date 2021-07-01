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
import com.epam.esm.service.converter.Converter;
import com.epam.esm.service.exception.InvalidCertificateException;
import com.epam.esm.service.exception.ServiceException;
import com.epam.esm.service.merger.Merger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
public class GiftCertificateServiceImpl implements GiftCertificateService {

	private static final int DEFAULT_ID = -1;
	private TagService tagService;
	private GiftCertificateRepository giftCertificateRepository;
	private Merger<GiftCertificate, GiftCertificateUpdateDTO> updateDtoIntoCertMerger;
	private Converter<FilterDTO, Filter> dtoToFilterConverter;
	private Converter<GiftCertificateCreateDTO, GiftCertificate> createDtoToCertConverter;
	private Converter<GiftCertificate, GiftCertificateOutputDTO> certToOutputDtoConverter;
	@Value("${cert.exception.not-found}")
	private String notFoundExceptionTemplate;
	private Converter<TagDTO, Tag> tagDtoToTagConverter;
	private Converter<Tag, TagDTO> tagToTagDtoConverter;
	public GiftCertificateServiceImpl(TagService tagService,
	                                  GiftCertificateRepository giftCertificateRepository,
	                                  Merger<GiftCertificate, GiftCertificateUpdateDTO> updateDtoIntoCertMerger,
	                                  Converter<FilterDTO, Filter> dtoToFilterConverter,
	                                  Converter<GiftCertificateCreateDTO, GiftCertificate> createDtoToCertConverter,
	                                  Converter<GiftCertificate, GiftCertificateOutputDTO> certToOutputDtoConverter,
	                                  Converter<TagDTO, Tag> tagDtoToTagConverter,
	                                  Converter<Tag, TagDTO> tagToTagDtoConverter) {
		this.tagService = tagService;
		this.giftCertificateRepository = giftCertificateRepository;
		this.updateDtoIntoCertMerger = updateDtoIntoCertMerger;
		this.dtoToFilterConverter = dtoToFilterConverter;
		this.createDtoToCertConverter = createDtoToCertConverter;
		this.certToOutputDtoConverter = certToOutputDtoConverter;
		this.tagDtoToTagConverter = tagDtoToTagConverter;
		this.tagToTagDtoConverter = tagToTagDtoConverter;
	}
	private InvalidCertificateException createNotFoundException(int id) {
		String identifier = "id=" + id;
		String message = String.format(notFoundExceptionTemplate, identifier);
		return new InvalidCertificateException(message, InvalidCertificateException.Reason.NOT_FOUND, id);
	}
	private void prepareTagsForCreateUpdate(GiftCertificate cert, Set<String> tagNames) {
		if(tagNames == null){
			return;
		}
		//get tags that are already exists in database
		Set<TagDTO> existingTagDTOs = tagService.getTagsFromNameSet(tagNames);
		Set<String> existingTagNames = existingTagDTOs.stream().map(TagDTO::getName).collect(Collectors.toSet());
		//get tag names that are not in database yet
		Set<String> newTagNames = tagNames.stream().filter(s -> !existingTagNames.contains(s))
				.collect(Collectors.toSet());
		//convert to tags existing tags
		Set<Tag> existingTags =
				existingTagDTOs.stream().map(tagDtoToTagConverter::convert).collect(Collectors.toSet());
		//convert to tags new tag names
		Set<Tag> newTags = newTagNames.stream().map(tn -> new Tag(null, tn)).collect(Collectors.toSet());
		Set<Tag> tags = Stream.concat(existingTags.stream(), newTags.stream()).collect(Collectors.toSet());
		cert.setTags(tags);
	}

	@Transactional
	public GiftCertificateOutputDTO createCertificate(GiftCertificateCreateDTO dto) {
		GiftCertificate input = createDtoToCertConverter.convert(dto);
		prepareTagsForCreateUpdate(input, dto.getTagNames());
		GiftCertificate output;
		try {
			output = giftCertificateRepository.createCertificate(input);
		} catch (DataAccessException ex) {
			throw new ServiceException(ex);
		}
		GiftCertificateOutputDTO outputDTO = certToOutputDtoConverter.convert(output);
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
		GiftCertificateOutputDTO dto = optionalCert.map(certToOutputDtoConverter::convert)
				.orElseThrow(() -> createNotFoundException(id));
		return dto;
	}

	@Transactional
	public void updateCertificate(int id, GiftCertificateUpdateDTO dto) {
		try {
			Optional<GiftCertificate> optionalCert = giftCertificateRepository.getCertificateById(id);
			GiftCertificate cert = optionalCert.orElseThrow(() -> createNotFoundException(id));
			GiftCertificate updatedCert = updateDtoIntoCertMerger.merge(cert, dto);
			prepareTagsForCreateUpdate(updatedCert, dto.getTagNames());
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
		Filter filter = dtoToFilterConverter.convert(filterDto);
		List<GiftCertificateOutputDTO> outputDTOList = new ArrayList<>();
		try {
			List<GiftCertificate> certs = giftCertificateRepository.getCertificatesByFilter(filter);
			for (GiftCertificate cert : certs) {
				GiftCertificateOutputDTO outputDTO = certToOutputDtoConverter.convert(cert);
				outputDTOList.add(outputDTO);
			}
		} catch (DataAccessException ex) {
			throw new ServiceException(ex);
		}
		return outputDTOList;
	}
}
