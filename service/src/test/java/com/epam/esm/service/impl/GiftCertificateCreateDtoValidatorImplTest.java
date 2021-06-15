package com.epam.esm.service.impl;

import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GiftCertificateCreateDtoValidatorImplTest {
	/*
	GiftCertificateValidator giftCertificateValidator;
	private int minNameLength;
	private int maxNameLength;
	private int minDescLength;
	private int maxDescLength;
	private int minDuration;
	private int maxDuration;
	private double minPrice;
	private double maxPrice;
	private int avgNameLength;
	private int avgDescLength;
	private int avgDuration;
	private double avgPrice;
	private String minName;
	private String avgName;
	private String maxName;
	private String minDesc;
	private String avgDesc;
	private String maxDesc;

	@BeforeAll
	public void initValidator() {
		ResourceBundle rb = ResourceBundle.getBundle("validation");
		giftCertificateValidator = new GiftCertificateCreateDtoValidatorImpl(rb);
		minNameLength = Integer.parseInt(rb.getString("cert.name.length.min"));
		maxNameLength = Integer.parseInt(rb.getString("cert.name.length.max"));
		minDescLength = Integer.parseInt(rb.getString("cert.desc.length.min"));
		maxDescLength = Integer.parseInt(rb.getString("cert.desc.length.max"));
		minDuration = Integer.parseInt(rb.getString("cert.duration.min"));
		maxDuration = Integer.parseInt(rb.getString("cert.duration.max"));
		minPrice = Double.parseDouble(rb.getString("cert.price.min"));
		maxPrice = Double.parseDouble(rb.getString("cert.price.max"));
		avgNameLength = (minNameLength + maxNameLength) / 2;
		avgDescLength = (minDescLength + maxDescLength) / 2;
		avgDuration = (minDuration + maxDuration) / 2;
		avgPrice = (minPrice + maxPrice) / 2;
		minName = RandomStringUtils.randomAlphabetic(minNameLength);
		avgName = RandomStringUtils.randomAlphabetic(avgNameLength);
		maxName = RandomStringUtils.randomAlphabetic(maxNameLength);
		minDesc = RandomStringUtils.randomAlphabetic(minDescLength);
		avgDesc = RandomStringUtils.randomAlphabetic(avgDescLength);
		maxDesc = RandomStringUtils.randomAlphabetic(maxDescLength);
	}


	private void assertStringParamsEquals(InvalidStringLengthException ex, String name) {
		assertEquals(ex.getLength(), (name == null) ? 0 : name.length());
		assertEquals(ex.getMinLength(), minNameLength);
		assertEquals(ex.getMaxLength(), maxNameLength);
	}

	private Stream<Arguments> positiveTestSource() {
		List<Arguments> tests = new ArrayList<>();
		tests.add(arguments(new GiftCertificateDTO(-1, avgName, avgDesc, avgPrice, avgDuration, null, null, null),
				false));
		tests.add(arguments(new GiftCertificateDTO(-1, minName, avgDesc, avgPrice, avgDuration, null, null, null),
				false));
		tests.add(arguments(new GiftCertificateDTO(-1, maxName, avgDesc, avgPrice, avgDuration, null, null, null),
				false));
		tests.add(arguments(new GiftCertificateDTO(-1, avgName, minDesc, avgPrice, avgDuration, null, null, null),
				false));
		tests.add(arguments(new GiftCertificateDTO(-1, avgName, maxDesc, avgPrice, avgDuration, null, null, null),
				false));
		tests.add(arguments(new GiftCertificateDTO(-1, avgName, avgDesc, minPrice, avgDuration, null, null, null),
				false));
		tests.add(arguments(new GiftCertificateDTO(-1, avgName, avgDesc, maxPrice, avgDuration, null, null, null),
				false));
		tests.add(arguments(new GiftCertificateDTO(-1, avgName, avgDesc, avgPrice, minDuration, null, null, null),
				false));
		tests.add(arguments(new GiftCertificateDTO(-1, avgName, avgDesc, avgPrice, maxDuration, null, null, null),
				false));
		tests.add(arguments(new GiftCertificateDTO(-1, null, avgDesc, avgPrice, avgDuration, null, null, null), true));
		tests.add(arguments(new GiftCertificateDTO(-1, avgName, null, avgPrice, avgDuration, null, null, null), true));
		tests.add(arguments(
				new GiftCertificateDTO(-1, avgName, avgDesc, GiftCertificateDTO.DEFAULT_PRICE, avgDuration, null, null,
						null), true));
		tests.add(arguments(
				new GiftCertificateDTO(-1, avgName, avgDesc, avgPrice, GiftCertificateDTO.DEFAULT_DURATION, null, null,
						null), true));
		return tests.stream();
	}

	private Stream<Arguments> invalidNameTestSource() {
		List<Arguments> tests = new ArrayList<>();
		tests.add(arguments(
				new GiftCertificateDTO(-1, minName.substring(1), avgDesc, avgPrice, avgDuration, null, null, null),
				false));
		tests.add(arguments(new GiftCertificateDTO(-1, maxName + " ", avgDesc, avgPrice, avgDuration, null, null, null),
				false));
		tests.add(arguments(new GiftCertificateDTO(-1, null, avgDesc, avgPrice, avgDuration, null, null, null), false));
		return tests.stream();
	}

	private Stream<Arguments> invalidDescriptionTestSource() {
		List<Arguments> tests = new ArrayList<>();
		tests.add(arguments(
				new GiftCertificateDTO(-1, avgName, minDesc.substring(1), avgPrice, avgDuration, null, null, null),
				false));
		tests.add(arguments(new GiftCertificateDTO(-1, avgName, maxDesc + " ", avgPrice, avgDuration, null, null, null),
				false));
		tests.add(arguments(new GiftCertificateDTO(-1, avgName, null, avgPrice, avgDuration, null, null, null), false));
		return tests.stream();
	}

	private Stream<Arguments> invalidDurationTestSource() {
		List<Arguments> tests = new ArrayList<>();
		tests.add(arguments(new GiftCertificateDTO(-1, avgName, avgDesc, avgPrice, minDuration - 1, null, null, null),
				false));
		tests.add(arguments(new GiftCertificateDTO(-1, avgName, avgDesc, avgPrice, maxDuration + 1, null, null, null),
				false));
		tests.add(arguments(
				new GiftCertificateDTO(-1, avgName, avgDesc, avgPrice, GiftCertificateDTO.DEFAULT_DURATION, null, null,
						null), false));
		return tests.stream();
	}

	private Stream<Arguments> invalidPriceTestSource() {
		List<Arguments> tests = new ArrayList<>();
		tests.add(arguments(new GiftCertificateDTO(-1, avgName, avgDesc, minPrice - 0.1, avgDuration, null, null, null),
				false));
		tests.add(arguments(new GiftCertificateDTO(-1, avgName, avgDesc, maxPrice + 0.1, avgDuration, null, null, null),
				false));
		tests.add(arguments(
				new GiftCertificateDTO(-1, avgName, avgDesc, GiftCertificateDTO.DEFAULT_PRICE, avgDuration, null, null,
						null), false));
		return tests.stream();
	}


	@ParameterizedTest
	@MethodSource("positiveTestSource")
	public void validCertTest(GiftCertificateDTO cert, boolean ignoreMissing) throws Exception {
		assertDoesNotThrow(() -> giftCertificateValidator.validateCertificate(cert, ignoreMissing));
	}

	@ParameterizedTest
	@MethodSource("invalidNameTestSource")
	public void invalidNameCertTest(GiftCertificateDTO cert, boolean ignoreMissing) throws Exception {
		InvalidCertificateNameException ex = assertThrows(InvalidCertificateNameException.class,
				() -> giftCertificateValidator.validateCertificate(cert, ignoreMissing));
		int length = (cert.getName() == null) ? 0 : cert.getName().length();
		assertEquals(ex.getLength(), length);
		assertEquals(ex.getMinLength(), minNameLength);
		assertEquals(ex.getMaxLength(), maxNameLength);
	}

	@ParameterizedTest
	@MethodSource("invalidDescriptionTestSource")
	public void invalidDescriptionCertTest(GiftCertificateDTO cert, boolean ignoreMissing) throws Exception {
		InvalidCertificateDescriptionException ex = assertThrows(InvalidCertificateDescriptionException.class,
				() -> giftCertificateValidator.validateCertificate(cert, ignoreMissing));
		int length = (cert.getDescription() == null) ? 0 : cert.getDescription().length();
		assertEquals(ex.getLength(), length);
		assertEquals(ex.getMinLength(), minDescLength);
		assertEquals(ex.getMaxLength(), maxDescLength);
	}

	@ParameterizedTest
	@MethodSource("invalidDurationTestSource")
	public void invalidDurationCertTest(GiftCertificateDTO cert, boolean ignoreMissing) throws Exception {
		InvalidCertificateDurationException ex = assertThrows(InvalidCertificateDurationException.class,
				() -> giftCertificateValidator.validateCertificate(cert, ignoreMissing));
		assertEquals(ex.getDuration(), cert.getDuration());
		assertEquals(ex.getMinDuration(), minDuration);
		assertEquals(ex.getMaxDuration(), maxDuration);
	}

	@ParameterizedTest
	@MethodSource("invalidPriceTestSource")
	public void invalidPriceCertTest(GiftCertificateDTO cert, boolean ignoreMissing) throws Exception {
		InvalidCertificatePriceException ex = assertThrows(InvalidCertificatePriceException.class,
				() -> giftCertificateValidator.validateCertificate(cert, ignoreMissing));
		assertEquals(ex.getPrice(), cert.getPrice());
		assertEquals(ex.getMinPrice(), minPrice);
		assertEquals(ex.getMaxPrice(), maxPrice);
	}

*/
}
