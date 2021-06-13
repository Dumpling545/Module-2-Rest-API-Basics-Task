package com.epam.esm.service.validator.impl;

import com.epam.esm.model.dto.GiftCertificateCreateDTO;
import com.epam.esm.service.exception.*;
import com.epam.esm.service.validator.GiftCertificateValidator;
import com.epam.esm.service.validator.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Supplier;

@Component
public class GiftCertificateCreateDtoValidatorImpl implements Validator<GiftCertificateCreateDTO> {
	@Value("${cert.minNameLength}")
	private int minNameLength;
	@Value("${cert.maxNameLength}")
	private int maxNameLength;
	@Value("${cert.minDescLength}")
	private int minDescLength;
	@Value("${cert.maxDescLength}")
	private int maxDescLength;
	@Value("${cert.minDuration}")
	private int minDuration;
	@Value("${cert.maxDuration}")
	private int maxDuration;
	@Value("${cert.minPrice}")
	private double minPrice;
	@Value("${cert.maxPrice}")
	private double maxPrice;

	@Override
	public void validate(GiftCertificateCreateDTO target) {
		if (target.getName() == null) {
			throw new InvalidCertificateNameException(0, minNameLength, maxNameLength);
		}
		if (target.getName().length() < minNameLength || target.getName().length() > maxNameLength) {
			throw new InvalidCertificateNameException(0, minNameLength, maxNameLength);
		}
		if (target.getDescription() == null) {
			throw new InvalidCertificateNameException(0, minNameLength, maxNameLength);
		}
		if (target.getDescription().length() < minDescLength || target.getDescription().length() > maxDescLength) {
			throw new InvalidCertificateNameException(0, minNameLength, maxNameLength);
		}
		if (target.getDuration() == null) {
			throw new InvalidCertificateNameException(0, minNameLength, maxNameLength);
		}
		if (target.getDuration() < minDuration || target.getDuration() > maxDuration) {
			throw new InvalidCertificateNameException(0, minNameLength, maxNameLength);
		}
		if (target.getPrice() == null) {
			throw new InvalidCertificateNameException(0, minNameLength, maxNameLength);
		}
		if (target.getPrice() < minPrice || target.getPrice() > maxPrice) {
			throw new InvalidCertificateNameException(0, minNameLength, maxNameLength);
		}
	}
}
