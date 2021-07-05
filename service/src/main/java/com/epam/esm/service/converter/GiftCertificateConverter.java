package com.epam.esm.service.converter;

import com.epam.esm.model.dto.GiftCertificateCreateDTO;
import com.epam.esm.model.dto.GiftCertificateOutputDTO;
import com.epam.esm.model.dto.GiftCertificateUpdateDTO;
import com.epam.esm.model.entity.GiftCertificate;
import com.epam.esm.model.entity.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.Set;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel="spring", uses = TagConverter.class, nullValuePropertyMappingStrategy = IGNORE)
public interface GiftCertificateConverter {
	GiftCertificate convert(GiftCertificateCreateDTO createDto, Set<Tag> tags);
	void mergeGiftCertificate(@MappingTarget GiftCertificate base, GiftCertificateUpdateDTO updateDto, Set<Tag> tags);
	GiftCertificateOutputDTO convert(GiftCertificate cert);
}
