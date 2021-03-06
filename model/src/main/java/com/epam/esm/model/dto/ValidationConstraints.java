package com.epam.esm.model.dto;

/**
 * Helper class containing constraints for Java Validation API
 */
public final class ValidationConstraints {
	public static final int MIN_TAG_NAME_LENGTH = 3;
	public static final int MAX_TAG_NAME_LENGTH = 30;
	public static final int MIN_CERT_NAME_LENGTH = 3;
	public static final int MAX_CERT_NAME_LENGTH = 50;
	public static final int MIN_CERT_DESC_LENGTH = 10;
	public static final int MAX_CERT_DESC_LENGTH = 200;
	public static final String MIN_CERT_PRICE = "0.01";
	public static final String MAX_CERT_PRICE = "1000000.0";
	public static final int MAX_CERT_PRICE_INTEGER_PART_SIZE = 7;
	public static final int MAX_CERT_PRICE_DECIMAL_PART_SIZE = 3;
	public static final int MIN_CERT_DURATION = 1;
	public static final int MAX_CERT_DURATION = 3650;
	public static final int MIN_PAGE_NUMBER = 1;
	public static final int MIN_PAGE_SIZE = 5;
	public static final int MAX_PAGE_SIZE = 100;

	private ValidationConstraints() {
	}
}
