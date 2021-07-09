package com.epam.esm.model.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Set;

import static com.epam.esm.model.dto.ValidationConstraints.MAX_CERT_DESC_LENGTH;
import static com.epam.esm.model.dto.ValidationConstraints.MAX_CERT_DURATION;
import static com.epam.esm.model.dto.ValidationConstraints.MAX_CERT_NAME_LENGTH;
import static com.epam.esm.model.dto.ValidationConstraints.MAX_CERT_PRICE;
import static com.epam.esm.model.dto.ValidationConstraints.MAX_CERT_PRICE_DECIMAL_PART_SIZE;
import static com.epam.esm.model.dto.ValidationConstraints.MAX_CERT_PRICE_INTEGER_PART_SIZE;
import static com.epam.esm.model.dto.ValidationConstraints.MAX_TAG_NAME_LENGTH;
import static com.epam.esm.model.dto.ValidationConstraints.MIN_CERT_DESC_LENGTH;
import static com.epam.esm.model.dto.ValidationConstraints.MIN_CERT_DURATION;
import static com.epam.esm.model.dto.ValidationConstraints.MIN_CERT_NAME_LENGTH;
import static com.epam.esm.model.dto.ValidationConstraints.MIN_CERT_PRICE;
import static com.epam.esm.model.dto.ValidationConstraints.MIN_TAG_NAME_LENGTH;

/**
 * DTO object encapsulating information for updates of Gift Certificates. Used for Web Layer -> Service layer
 * communication
 */
@Data
@Builder
public class GiftCertificateUpdateDTO {
	@Size(min = MIN_CERT_NAME_LENGTH, max = MAX_CERT_NAME_LENGTH, message = "{cert.validation-message.name-size}")
	private String name;

	@Size(min = MIN_CERT_DESC_LENGTH, max = MAX_CERT_DESC_LENGTH, message = "{cert.validation-message.description-size}")
	private String description;

	@DecimalMin(value = MIN_CERT_PRICE, message = "{cert.validation-message.price-min}")
	@DecimalMax(value = MAX_CERT_PRICE, message = "{cert.validation-message.price-max}")
	@Digits(integer = MAX_CERT_PRICE_INTEGER_PART_SIZE, fraction = MAX_CERT_PRICE_DECIMAL_PART_SIZE, message = "{cert.validation-message.price-digits}")
	private BigDecimal price;

	@Min(value = MIN_CERT_DURATION, message = "{cert.validation-message.duration-min}")
	@Max(value = MAX_CERT_DURATION, message = "{cert.validation-message.duration-max}")
	private Integer duration;

	private Set<@NotNull(message = "{tag.validation-message.name-not-empty}") @Size(min = MIN_TAG_NAME_LENGTH, max = MAX_TAG_NAME_LENGTH, message="{tag.validation-message.name-size}") String> tagNames;
}