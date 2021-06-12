package com.epam.esm.model.dto;

import java.io.Serial;
import java.io.Serializable;

/**
 * DTO Object used for wrapping info about exceptional situations into user-readable form before JSON marshalling
 */
public class Error implements Serializable {
	@Serial
	private static final long serialVersionUID = 8914803538875703513L;
	private int errorCode;
	private String errorMessage;

	public Error() {
	}

	public Error(int errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Error error = (Error) o;

		if (errorCode != error.errorCode) {
			return false;
		}
		return errorMessage != null ? errorMessage.equals(error.errorMessage) : error.errorMessage == null;
	}

	@Override
	public int hashCode() {
		int result = errorCode;
		result = 31 * result + (errorMessage != null ? errorMessage.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "Error{" + "errorCode=" + errorCode + ", errorMessage='" + errorMessage + '\'' + '}';
	}
}
