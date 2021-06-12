package com.epam.esm.service.exception;

import java.io.Serial;

/**
 * Exception that will be thrown if provided Gift Certificate has duration not in valid duration range.
 */
public class InvalidCertificateDurationException extends ServiceException {
	@Serial
	private static final long serialVersionUID = 2687637447476432084L;
	private final int duration;
	private final int minDuration;
	private final int maxDuration;

	public InvalidCertificateDurationException(int duration, int minDuration, int maxDuration) {
		this.duration = duration;
		this.minDuration = minDuration;
		this.maxDuration = maxDuration;
	}

	public int getDuration() {
		return duration;
	}

	public int getMinDuration() {
		return minDuration;
	}

	public int getMaxDuration() {
		return maxDuration;
	}
}
