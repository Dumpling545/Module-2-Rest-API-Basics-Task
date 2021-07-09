package com.epam.esm.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
public class OrderDTO extends IdentifiableDTO {
	@NotNull(message = "{order.validation-message.user-id-not-empty}")
	private Integer userId;
	@NotNull(message = "{order.validation-message.gift-certificate-id-not-empty}")
	private Integer giftCertificateId;
	private BigDecimal cost;
	private LocalDateTime purchaseDate;
}
