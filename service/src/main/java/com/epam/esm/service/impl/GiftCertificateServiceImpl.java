package com.epam.esm.service.impl;

import com.epam.esm.db.GiftCertificateRepository;
import com.epam.esm.model.dto.GiftCertificateCreateDTO;
import com.epam.esm.model.dto.GiftCertificateOutputDTO;
import com.epam.esm.model.dto.GiftCertificateSearchFilterDTO;
import com.epam.esm.model.dto.GiftCertificateUpdateDTO;
import com.epam.esm.model.dto.PageDTO;
import com.epam.esm.model.dto.PagedResultDTO;
import com.epam.esm.model.dto.TagDTO;
import com.epam.esm.model.entity.GiftCertificate;
import com.epam.esm.model.entity.GiftCertificateSearchFilter;
import com.epam.esm.model.entity.PagedResult;
import com.epam.esm.model.entity.Tag;
import com.epam.esm.service.GiftCertificateService;
import com.epam.esm.service.TagService;
import com.epam.esm.service.converter.GiftCertificateConverter;
import com.epam.esm.service.converter.GiftCertificateSearchFilterConverter;
import com.epam.esm.service.converter.PagedResultConverter;
import com.epam.esm.service.converter.TagConverter;
import com.epam.esm.service.exception.InvalidCertificateException;
import com.epam.esm.service.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
@RequiredArgsConstructor
public class GiftCertificateServiceImpl implements GiftCertificateService {

    private final TagService tagService;
    private final GiftCertificateRepository giftCertificateRepository;
    private final GiftCertificateConverter giftCertificateConverter;
    private final TagConverter tagConverter;
    private final GiftCertificateSearchFilterConverter giftCertificateSearchFilterConverter;
    private final PagedResultConverter pagedResultConverter;
    @Value("${cert.exception.not-found}")
    private String notFoundExceptionTemplate;

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
        return Stream.concat(existingTags.stream(), newTags.stream()).collect(Collectors.toSet());
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
        return giftCertificateConverter.convert(output);
    }

    @Override
    public GiftCertificateOutputDTO getCertificate(int id) {
        Optional<GiftCertificate> optionalCert;
        try {
            optionalCert = giftCertificateRepository.getCertificateById(id);
        } catch (DataAccessException ex) {
            throw new ServiceException(ex);
        }
        return optionalCert.map(giftCertificateConverter::convert)
                .orElseThrow(() -> createNotFoundException(id));
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
    public PagedResultDTO<GiftCertificateOutputDTO> getCertificates(
            GiftCertificateSearchFilterDTO giftCertificateSearchFilterDTO,
            PageDTO pageDTO) {
        GiftCertificateSearchFilter giftCertificateSearchFilter = giftCertificateSearchFilterConverter.convert(
                giftCertificateSearchFilterDTO);
        PagedResult<GiftCertificate> pagedResult;
        try {
            pagedResult = giftCertificateRepository
                    .getCertificatesByFilter(giftCertificateSearchFilter, pageDTO.getOffset(), pageDTO.getPageSize());
        } catch (DataAccessException ex) {
            throw new ServiceException(ex);
        }
        return pagedResultConverter.convertToCertificatePage(pagedResult);
    }
}
