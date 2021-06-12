package com.epam.esm.model.dto;

import com.epam.esm.model.entity.Filter;

import java.io.Serial;
import java.io.Serializable;

/**
 * DTO object encapsulating information about Gift Certificate filters. Used for Web Layer <-> Service layer
 * communication
 */
public class FilterDTO extends Filter {

	@Serial
	private static final long serialVersionUID = -1276074712658482017L;

	private String tagName;

	public FilterDTO() {
	}

	public FilterDTO(String namePart, String descriptionPart, SortOption sortBy, String tagName) {
		super(namePart, descriptionPart, -1, sortBy);
		this.tagName = tagName;
	}

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}
}
