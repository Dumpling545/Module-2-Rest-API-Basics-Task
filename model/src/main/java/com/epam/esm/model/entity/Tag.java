package com.epam.esm.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


/**
 * Entity object encapsulating information about Tag. Used for Service Layer <-> Repository layer communication
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tag")
public class Tag {
	private static final Logger logger = LoggerFactory.getLogger(Tag.class);
	@Id
	@Column(nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Integer id;
	@Column(nullable = false)
	String name;
	@PrePersist
	public void onPrePersist(){
		logger.info(toString() + " to be persisted");
	}
	@PreRemove
	public void onPreRemove(){
		logger.info(toString() + " to be removed");
	}
	@PreUpdate
	public void onPreUpdate(){
		logger.info(toString() + " to be updated");
	}
}
