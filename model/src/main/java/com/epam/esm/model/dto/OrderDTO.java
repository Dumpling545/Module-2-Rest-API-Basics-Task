package com.epam.esm.model.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class OrderDTO {
	Integer id;
	@NotNull(message = "{order.validation-message.user-id-not-empty}")
	Integer userId;
	@NotNull(message = "{order.validation-message.gift-certificate-id-not-empty}")
	Integer giftCertificateId;
	BigDecimal cost;
	LocalDateTime purchaseDate;
}
