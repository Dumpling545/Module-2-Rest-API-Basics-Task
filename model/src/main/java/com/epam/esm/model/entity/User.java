package com.epam.esm.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

/**
 * Entity object encapsulating information about User. Used for Service Layer <-> Repository layer communication
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "cert_user")
public class User {
	private static final Logger logger = LoggerFactory.getLogger(User.class);
	@Id
	@Column(nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Column(name = "user_name", nullable = false, unique = true)
	private String name;
	@Column
	private String password;
	@Column(name = "external_provider")
	private String externalProvider;
	@Column(name = "external_id")
	private String externalId;
	@Enumerated(EnumType.STRING)
	private Role role;

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

	public enum Role {
		USER, ADMIN;
	}
}
