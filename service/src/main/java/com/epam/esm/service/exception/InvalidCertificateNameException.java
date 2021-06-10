package com.epam.esm.service.exception;

import java.io.Serial;

/**
 * Exception that will be thrown if provided Gift Certificate has invalid name
 * length (too short or too long). Depending on context, null name also can be
 * treated as name of length 0
 */
public class InvalidCertificateNameException
		extends InvalidStringLengthException
{
	@Serial
	private static final long serialVersionUID = 4641029563325020830L;

	public InvalidCertificateNameException(int length, int minLength,
	                                       int maxLength)
	{
		super(length, minLength, maxLength);
	}
}
