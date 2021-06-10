package com.epam.esm.model.entity;

import java.io.Serial;
import java.io.Serializable;

/**
 * Entity object encapsulating information about Tag. Used for Service Layer <->
 * Repository layer communication
 */
public class Tag implements Serializable {
	@Serial
	private static final long serialVersionUID = -6974624612266562569L;
	private int id = -1;
	private String name;

	public Tag() {
	}

	public Tag(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public Tag(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Tag tag = (Tag) o;

		if (id != tag.id) {
			return false;
		}
		return name != null ? name.equals(tag.name) : tag.name == null;
	}

	@Override
	public int hashCode() {
		int result = id;
		result = 31 * result + (name != null ? name.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "Tag{" +
				"id=" + id +
				", name='" + name + '\'' +
				'}';
	}
}
