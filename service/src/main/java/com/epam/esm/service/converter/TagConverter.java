package com.epam.esm.service.converter;

import com.epam.esm.model.dto.TagDTO;
import com.epam.esm.model.entity.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel="spring")
public interface TagConverter {
	@Mapping(target = "id", ignore = true)
	Tag convert(TagDTO dto);
	TagDTO convert(Tag tag);
}
