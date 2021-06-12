package com.epam.esm.service.converter.impl;

import com.epam.esm.model.dto.FilterDTO;
import com.epam.esm.model.dto.TagDTO;
import com.epam.esm.model.entity.Filter;
import com.epam.esm.model.entity.Tag;
import com.epam.esm.service.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class TagDtoToTagConverter implements Converter<TagDTO, Tag> {

	@Override
	public Tag convert(TagDTO tagDTO) {
		Tag tag = new Tag(tagDTO.getId(), tagDTO.getName());
		return tag;
	}
}
