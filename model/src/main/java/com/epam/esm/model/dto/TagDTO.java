package com.epam.esm.model.dto;

import com.epam.esm.model.entity.Tag;

import java.io.Serial;

/**
 * DTO object encapsulating information about Tag. Used for Web Layer <-> Service layer communication
 */
public class TagDTO extends Tag {
	@Serial
	private static final long serialVersionUID = -5181818300779954457L;

	public TagDTO() {
	}

	public TagDTO(Tag tag) {
		super(tag.getId(), tag.getName());
	}

	public TagDTO(int id, String name) {
		super(id, name);
	}

	public TagDTO(String name) {
		super(name);
	}
}
