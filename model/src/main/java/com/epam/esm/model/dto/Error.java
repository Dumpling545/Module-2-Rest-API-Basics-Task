package com.epam.esm.model.dto;

import lombok.Value;

import java.io.Serial;
import java.io.Serializable;

/**
 * DTO Object used for wrapping info about exceptional situations into user-readable form before JSON marshalling
 */
@Value
public class Error {
	int errorCode;
	String errorMessage;
}
