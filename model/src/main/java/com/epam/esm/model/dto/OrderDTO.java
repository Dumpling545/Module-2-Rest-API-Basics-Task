package com.epam.esm.model.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class OrderDTO {
	private Integer id;
	@NotNull(message = "{order.validation-message.user-id-not-empty}")
	private Integer userId;
	@NotNull(message = "{order.validation-message.gift-certificate-id-not-empty}")
	private Integer giftCertificateId;
	private BigDecimal cost;
	private LocalDateTime purchaseDate;
}
