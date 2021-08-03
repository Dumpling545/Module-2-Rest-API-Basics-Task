package com.epam.esm.model.dto;

import lombok.Value;

import java.util.List;

/**
 * DTO Object used for wrapping info about exceptional situations into user-readable form before JSON marshalling
 */
@Value
public class Error {
	int errorCode;
	List<String> errorMessages;
}
