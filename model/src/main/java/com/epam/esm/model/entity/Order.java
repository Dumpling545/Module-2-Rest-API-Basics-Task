package com.epam.esm.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "cert_order")
public class Order {
	private static final Logger logger = LoggerFactory.getLogger(Order.class);
	@Id
	@Column(nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Integer id;
	@Column(name = "user_id", nullable = false)
	Integer userId;
	@Column(name = "gift_certificate_id", nullable = false)
	Integer giftCertificateId;
	@Column(nullable = false)
	BigDecimal cost;
	@Column(name = "purchase_date", nullable = false)
	LocalDateTime purchaseDate;
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
