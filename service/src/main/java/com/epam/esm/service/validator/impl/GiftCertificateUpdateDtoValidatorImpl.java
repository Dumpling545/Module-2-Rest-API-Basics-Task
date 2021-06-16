package com.epam.esm.service.validator.impl;

import com.epam.esm.model.dto.GiftCertificateUpdateDTO;
import com.epam.esm.service.exception.InvalidCertificateException;
import com.epam.esm.service.validator.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GiftCertificateUpdateDtoValidatorImpl implements Validator<GiftCertificateUpdateDTO> {
	@Value("${cert.name.length.min}")
	private int minNameLength;
	@Value("${cert.name.length.max}")
	private int maxNameLength;
	@Value("${cert.desc.length.min}")
	private int minDescLength;
	@Value("${cert.desc.length.max}")
	private int maxDescLength;
	@Value("${cert.duration.min}")
	private int minDuration;
	@Value("${cert.duration.max}")
	private int maxDuration;
	@Value("${cert.price.min}")
	private double minPrice;
	@Value("${cert.price.max}")
	private double maxPrice;

	@Value("${cert.exception.name.out-of-bounds}")
	private String outOfBoundsNameTemplate;
	@Value("${cert.exception.desc.out-of-bounds}")
	private String outOfBoundsDescTemplate;
	@Value("${cert.exception.duration.out-of-bounds}")
	private String outOfBoundsDurationTemplate;
	@Value("${cert.exception.price.out-of-bounds}")
	private String outOfBoundsPriceTemplate;

	@Override
	public void validate(GiftCertificateUpdateDTO target) {
		if (target.getName() != null &&
				(target.getName().length() < minNameLength || target.getName().length() > maxNameLength)) {
			String message =
					String.format(outOfBoundsNameTemplate, target.getName().length(), minNameLength, maxNameLength);
			throw new InvalidCertificateException(message, InvalidCertificateException.Reason.INVALID_NAME);
		}
		if (target.getDescription() != null && (target.getDescription().length() < minDescLength ||
				target.getDescription().length() > maxDescLength)) {
			String message = String.format(outOfBoundsDescTemplate, target.getDescription().length(), minDescLength,
					maxDescLength);
			throw new InvalidCertificateException(message, InvalidCertificateException.Reason.INVALID_DESCRIPTION);
		}
		if (target.getDuration() != null &&
				(target.getDuration() < minDuration || target.getDuration() > maxDuration)) {
			String message = String.format(outOfBoundsDurationTemplate, target.getDuration(), minDuration, maxDuration);
			throw new InvalidCertificateException(message, InvalidCertificateException.Reason.INVALID_DURATION);
		}
		if (target.getPrice() != null && (target.getPrice() < minPrice || target.getPrice() > maxPrice)) {
			throw new InvalidCertificateException(outOfBoundsPriceTemplate,
					InvalidCertificateException.Reason.INVALID_PRICE);
		}
	}
}
