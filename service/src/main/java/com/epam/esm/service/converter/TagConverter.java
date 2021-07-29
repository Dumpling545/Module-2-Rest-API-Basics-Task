package com.epam.esm.service.converter;

import com.epam.esm.model.dto.TagDTO;
import com.epam.esm.model.entity.Tag;
import org.mapstruct.Mapper;

/**
 * Interface mapping tag entities and DTOs. Used by MapStruct to generate actual mapper class
 *
 * @see <a href="https://mapstruct.org/">MapStruct library</a>
 */
@Mapper(componentModel = "spring")
public interface TagConverter {
	Tag convert(TagDTO dto);

	TagDTO convert(Tag tag);
}
