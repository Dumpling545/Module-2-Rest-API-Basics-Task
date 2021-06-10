package com.epam.esm.service.validator.impl;

import com.epam.esm.model.dto.GiftCertificateDTO;
import com.epam.esm.model.entity.GiftCertificate;
import com.epam.esm.service.exception.*;
import com.epam.esm.service.validator.GiftCertificateValidator;

import java.util.*;
import java.util.function.Supplier;

public class GiftCertificateValidatorImpl implements GiftCertificateValidator {
	private final String MIN_NAME_LENGTH_KEY = "cert.minNameLength";
	private final int MIN_NAME_LENGTH;
	private final String MAX_NAME_LENGTH_KEY = "cert.maxNameLength";
	private final int MAX_NAME_LENGTH;
	private final String MIN_DESC_LENGTH_KEY = "cert.minDescLength";
	private final int MIN_DESC_LENGTH;
	private final String MAX_DESC_LENGTH_KEY = "cert.maxDescLength";
	private final int MAX_DESC_LENGTH;
	private final String MIN_DURATION_KEY = "cert.minDuration";
	private final int MIN_DURATION;
	private final String MAX_DURATION_KEY = "cert.maxDuration";
	private final int MAX_DURATION;
	private final String MIN_PRICE_KEY = "cert.minPrice";
	private final double MIN_PRICE;
	private final String MAX_PRICE_KEY = "cert.maxPrice";
	private final double MAX_PRICE;


	private static final List<Integer> IGNORED_DURATIONS = new ArrayList<>();
	private static final List<Double> IGNORED_PRICES = new ArrayList<>();

	static {
		IGNORED_DURATIONS.add(GiftCertificateDTO.DEFAULT_DURATION);
		IGNORED_PRICES.add(GiftCertificateDTO.DEFAULT_PRICE);
	}

	public GiftCertificateValidatorImpl(ResourceBundle bounds) {
		MIN_NAME_LENGTH =
				Integer.parseInt(bounds.getString(MIN_NAME_LENGTH_KEY));
		MAX_NAME_LENGTH =
				Integer.parseInt(bounds.getString(MAX_NAME_LENGTH_KEY));
		MIN_DESC_LENGTH =
				Integer.parseInt(bounds.getString(MIN_DESC_LENGTH_KEY));
		MAX_DESC_LENGTH =
				Integer.parseInt(bounds.getString(MAX_DESC_LENGTH_KEY));
		MIN_DURATION = Integer.parseInt(bounds.getString(MIN_DURATION_KEY));
		MAX_DURATION = Integer.parseInt(bounds.getString(MAX_DURATION_KEY));
		MIN_PRICE = Double.parseDouble(bounds.getString(MIN_PRICE_KEY));
		MAX_PRICE = Double.parseDouble(bounds.getString(MAX_PRICE_KEY));
	}

	private static <T extends Comparable<T>> void outOfRangeCheck(T value,
	                                                              T min,
	                                                              T max,
	                                                              List<T> ignoredValues,
	                                                              Supplier<ServiceException> exceptionSupplier)
			throws ServiceException
	{
		if (ignoredValues.stream()
				.anyMatch(v -> v.compareTo(value) == 0))
		{
			return;
		}
		int valMinComp = value.compareTo(min);
		if (valMinComp < 0) {
			throw exceptionSupplier.get();
		}
		int valMaxComp = value.compareTo(max);
		if (valMaxComp > 0) {
			throw exceptionSupplier.get();
		}

	}

	@Override
	public void validateCertificate(GiftCertificateDTO certificate,
	                                boolean ignoreMissing)
	{
		List<Integer> ignoredDurations = Collections.EMPTY_LIST;
		List<Double> ignoredPrices = Collections.EMPTY_LIST;
		if (ignoreMissing) {
			ignoredDurations = IGNORED_DURATIONS;
			ignoredPrices = IGNORED_PRICES;
		}
		if (certificate.getName() == null && !ignoreMissing) {
			throw new InvalidCertificateNameException(0, MIN_NAME_LENGTH,
					MAX_NAME_LENGTH);
		}
		if (certificate.getDescription() == null && !ignoreMissing) {
			throw new InvalidCertificateDescriptionException(0, MIN_DESC_LENGTH,
					MAX_DESC_LENGTH);
		}
		if (certificate.getName() != null) {
			int length = certificate.getName().length();
			outOfRangeCheck(length, MIN_NAME_LENGTH, MAX_NAME_LENGTH,
					Collections.EMPTY_LIST, () ->
							new InvalidCertificateNameException(length,
									MIN_NAME_LENGTH, MAX_NAME_LENGTH)
			);
		}
		if (certificate.getDescription() != null) {
			int length = certificate.getDescription().length();
			outOfRangeCheck(length, MIN_DESC_LENGTH, MAX_DESC_LENGTH,
					Collections.EMPTY_LIST, () ->
							new InvalidCertificateDescriptionException(length,
									MIN_DESC_LENGTH, MAX_DESC_LENGTH)
			);
		}
		outOfRangeCheck(certificate.getDuration(), MIN_DURATION, MAX_DURATION,
				ignoredDurations, () ->
						new InvalidCertificateDurationException(
								certificate.getDuration(),
								MIN_DURATION, MAX_DURATION)
		);
		outOfRangeCheck(certificate.getPrice(), MIN_PRICE, MAX_PRICE,
				ignoredPrices, () ->
						new InvalidCertificatePriceException(
								certificate.getPrice(),
								MIN_PRICE, MAX_PRICE)
		);
	}


}
