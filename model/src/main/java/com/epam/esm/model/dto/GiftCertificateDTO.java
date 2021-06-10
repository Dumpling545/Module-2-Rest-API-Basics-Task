package com.epam.esm.model.dto;

import com.epam.esm.model.entity.GiftCertificate;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * DTO object encapsulating information about Gift Certificates. Used for Web
 * Layer -> Service layer communication
 */
public class GiftCertificateDTO extends GiftCertificate {
	private List<String> tags = Collections.EMPTY_LIST;
	@Serial
	private static final long serialVersionUID = -888592341135939674L;

	public GiftCertificateDTO() {
		setDuration(DEFAULT_DURATION);
		setPrice(DEFAULT_PRICE);
	}

	public GiftCertificateDTO(List<String> tags) {
		this();
		this.tags = tags;
	}

	public static int DEFAULT_DURATION = -1;
	public static double DEFAULT_PRICE = -1;

	public GiftCertificateDTO(int id, String name, String description,
	                          double price,
	                          int duration, LocalDateTime createDate,
	                          LocalDateTime lastUpdateDate,
	                          List<String> tags)
	{
		super(id, name, description, price, duration, createDate,
				lastUpdateDate);
		this.tags = tags;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		if (!super.equals(o)) {
			return false;
		}

		GiftCertificateDTO that = (GiftCertificateDTO) o;

		return tags != null ? tags.equals(that.tags) : that.tags == null;
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + (tags != null ? tags.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		String base = super.toString();
		String openBody = base.substring(base.indexOf('{'), base.length() - 1);
		return "GiftCertificateDTO" +
				openBody + ", tags=" + tags +
				'}';
	}
}
