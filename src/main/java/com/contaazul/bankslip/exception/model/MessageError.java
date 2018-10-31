package com.contaazul.bankslip.exception.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum  MessageError {
    RESOURCE_NOT_FOUND("Bankslip not found with the specified id"),
    METHOD_NOT_ALLOWED("Method Not Allowed"),
    BAD_REQUEST("Bad request"),
    UNPROCESSABLE_ENTITY("Invalid bankslip provided.The possible reasons are: A field of the provided bankslip was null or with invalid values"),
	CONFLICT("Resource already exists"),
	NO_CONTENT("No Content");
	
    private final String msg;
}
