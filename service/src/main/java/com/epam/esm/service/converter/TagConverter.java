package com.epam.esm.service.converter;

import com.epam.esm.model.dto.TagDTO;
import com.epam.esm.model.entity.Tag;
import org.mapstruct.Mapper;

@Mapper(componentModel="spring")
public interface TagConverter {
	Tag convert(TagDTO dto);
	TagDTO convert(Tag tag);
}
