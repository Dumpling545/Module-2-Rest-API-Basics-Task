package com.epam.esm.service.converter.impl;

import com.epam.esm.model.dto.TagDTO;
import com.epam.esm.model.entity.Tag;
import com.epam.esm.service.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class TagToTagDtoConverter implements Converter<Tag, TagDTO> {
	@Override
	public TagDTO convert(Tag tag) {
		TagDTO dto = new TagDTO(tag.getId(), tag.getName());
		return dto;
	}
}
