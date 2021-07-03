package com.epam.esm.service.converter;

import com.epam.esm.model.dto.GiftCertificateCreateDTO;
import com.epam.esm.model.dto.GiftCertificateOutputDTO;
import com.epam.esm.model.dto.GiftCertificateUpdateDTO;
import com.epam.esm.model.entity.GiftCertificate;
import com.epam.esm.model.entity.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;

@Mapper(componentModel="spring", uses = TagConverter.class)
public interface GiftCertificateConverter {
	GiftCertificate convert(GiftCertificateCreateDTO createDto, Set<Tag> tags);
	@Mapping(source = "updateDto.name", target = "name", defaultExpression = "java(base.getName())")
	@Mapping(source = "updateDto.description", target = "description", defaultExpression = "java(base.getDescription())")
	@Mapping(source = "updateDto.price", target = "price", defaultExpression = "java(base.getPrice())")
	@Mapping(source = "updateDto.duration", target = "duration", defaultExpression = "java(base.getDuration())")
	@Mapping(source = "tags", target = "tags", defaultExpression = "java(base.getTags())")
	GiftCertificate convert(GiftCertificate base, GiftCertificateUpdateDTO updateDto, Set<Tag> tags);
	GiftCertificateOutputDTO convert(GiftCertificate cert);
}
