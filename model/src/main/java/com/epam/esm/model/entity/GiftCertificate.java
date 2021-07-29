package com.epam.esm.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * Entity object encapsulating information about Gift Certificate. Used for Service Layer <-> Repository layer
 * communication
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table(name = "gift_certificate")
@NamedEntityGraph(
		name = "full-certificate-entity-graph",
		attributeNodes = {
				@NamedAttributeNode(GiftCertificate_.NAME),
				@NamedAttributeNode(GiftCertificate_.DESCRIPTION),
				@NamedAttributeNode(GiftCertificate_.PRICE),
				@NamedAttributeNode(GiftCertificate_.DURATION),
				@NamedAttributeNode(GiftCertificate_.CREATE_DATE),
				@NamedAttributeNode(GiftCertificate_.LAST_UPDATE_DATE),
				@NamedAttributeNode(GiftCertificate_.TAGS)
		}
)
public class GiftCertificate {
	private static final Logger logger = LoggerFactory.getLogger(GiftCertificate.class);
	@Id
	@Column(nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Column(nullable = false)
	private String name;
	@Column(nullable = false)
	private String description;
	@Column(nullable = false)
	private BigDecimal price;
	@Column(nullable = false)
	private Integer duration;
	@Column(name = "create_date", nullable = false)
	private LocalDateTime createDate;
	@Column(name = "last_update_date", nullable = false)
	private LocalDateTime lastUpdateDate;
	@ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST})
	@JoinTable(name = "tag_gift_certificate",
	           joinColumns = @JoinColumn(name = "gift_certificate_id"),
	           inverseJoinColumns = @JoinColumn(name = "tag_id"))
	private Set<Tag> tags;

	@PrePersist
	public void onPrePersist() {
		logger.info(toString() + " to be persisted");
	}

	@PreRemove
	public void onPreRemove() {
		logger.info(toString() + " to be removed");
	}

	@PreUpdate
	public void onPreUpdate() {
		logger.info(toString() + " to be updated");
	}
}
