package com.epam.esm.service.exception;

import java.io.Serial;

/**
 * Exception that will be thrown if provided Gift Certificate has price not in valid price range.
 */
public class InvalidCertificatePriceException extends ServiceException {
	@Serial
	private static final long serialVersionUID = 4009904025119496752L;
	private final double price;
	private final double minPrice;
	private final double maxPrice;

	public InvalidCertificatePriceException(double price, double minPrice, double maxPrice) {
		this.price = price;
		this.minPrice = minPrice;
		this.maxPrice = maxPrice;
	}

	public double getPrice() {
		return price;
	}

	public double getMinPrice() {
		return minPrice;
	}

	public double getMaxPrice() {
		return maxPrice;
	}
}
