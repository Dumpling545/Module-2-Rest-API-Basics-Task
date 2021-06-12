package com.epam.esm.model.entity;

import java.io.Serial;
import java.io.Serializable;

/**
 * Entity object encapsulating information about Gift Certificate filters. Used for Service Layer <-> Repository layer
 * communication
 */
public class Filter implements Serializable {

	public enum SortOption {
		NAME_ASC("ORDER BY name ASC"), NAME_DESC("ORDER BY name DESC"), CREATE_DATE_ASC("ORDER BY create_date ASC"),
		CREATE_DATE_DESC("ORDER BY create_date DESC"), LAST_UPDATE_DATE_ASC("ORDER BY last_update_date ASC"),
		LAST_UPDATE_DATE_DESC("ORDER BY last_update_date DESC");
		private String sql;

		SortOption(String sql) {
			this.sql = sql;
		}

		public String getSql() {
			return sql;
		}
	}

	@Serial
	private static final long serialVersionUID = -5704001337195282389L;
	public static final int NO_TAG_SPECIFIED = -1;
	private String namePart;
	private String descriptionPart;
	private int tagId = NO_TAG_SPECIFIED;
	private SortOption sortBy;

	public Filter() {
	}

	public Filter(String namePart, String descriptionPart, int tagId, SortOption sortBy) {
		this.namePart = namePart;
		this.descriptionPart = descriptionPart;
		this.tagId = tagId;
		this.sortBy = sortBy;
	}


	public String getNamePart() {
		return namePart;
	}

	public void setNamePart(String namePart) {
		this.namePart = namePart;
	}

	public String getDescriptionPart() {
		return descriptionPart;
	}

	public void setDescriptionPart(String descriptionPart) {
		this.descriptionPart = descriptionPart;
	}

	public int getTagId() {
		return tagId;
	}

	public void setTagId(int tagId) {
		this.tagId = tagId;
	}

	public SortOption getSortBy() {
		return sortBy;
	}

	public void setSortBy(SortOption sortBy) {
		this.sortBy = sortBy;
	}

}
