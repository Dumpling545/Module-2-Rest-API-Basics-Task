package com.epam.esm.model.dto;

import com.epam.esm.model.entity.GiftCertificate;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO object encapsulating information about Gift Certificates. Used for Web Layer <- Service layer communication
 */
public class GiftCertificateOutputDTO extends GiftCertificate {

	@Serial
	private static final long serialVersionUID = -8493048028853542233L;
	private List<TagDTO> tags;

	public GiftCertificateOutputDTO() {
	}

	public GiftCertificateOutputDTO(GiftCertificate giftCertificate, List<TagDTO> tags) {
		setId(giftCertificate.getId());
		setName(giftCertificate.getName());
		setDescription(giftCertificate.getDescription());
		setDuration(giftCertificate.getDuration());
		setPrice(giftCertificate.getPrice());
		setCreateDate(giftCertificate.getCreateDate());
		setLastUpdateDate(giftCertificate.getLastUpdateDate());
		setTags(tags);
	}

	public GiftCertificateOutputDTO(int id, String name, String description, double price, int duration,
	                                LocalDateTime createDate, LocalDateTime lastUpdateDate, List<TagDTO> tags) {
		super(id, name, description, price, duration, createDate, lastUpdateDate);
		this.tags = tags;
	}

	public List<TagDTO> getTags() {
		return tags;
	}

	public void setTags(List<TagDTO> tags) {
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

		GiftCertificateOutputDTO that = (GiftCertificateOutputDTO) o;

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
		return "GiftCertificateOutputDTO{" + "tags=" + tags + '}';
	}
}
