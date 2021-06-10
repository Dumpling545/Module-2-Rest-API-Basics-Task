package com.epam.esm.service.exception;

import java.io.Serial;

/**
 * Exception that will be thrown if provided Gift Certificate has invalid
 * description length (too short or too long). Depending on context, null
 * description also can be treated as description of length 0
 */
public class InvalidCertificateDescriptionException
		extends InvalidStringLengthException
{
	@Serial
	private static final long serialVersionUID = -660385090231049939L;

	public InvalidCertificateDescriptionException(int length,
	                                              int minLength,
	                                              int maxLength)
	{
		super(length, minLength, maxLength);
	}
}
