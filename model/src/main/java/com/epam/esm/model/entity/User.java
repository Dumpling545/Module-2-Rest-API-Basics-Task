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

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user")
public class User {
	private static final Logger logger = LoggerFactory.getLogger(User.class);
	@Id
	@Column(nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Integer id;
	@Column(name = "user_name", nullable = false, unique = true)
	String userName;
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
