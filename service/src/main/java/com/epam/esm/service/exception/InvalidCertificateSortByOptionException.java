package com.epam.esm.service.exception;

public class InvalidCertificateSortByOptionException extends ServiceException {
	public InvalidCertificateSortByOptionException(String message){
		super(message);
	}

	public InvalidCertificateSortByOptionException(String message, Throwable cause) {
		super(message, cause);
	}
}
