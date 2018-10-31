package com.contaazul.bankslip.exception.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum  MessageError {
    RESOURCE_NOT_FOUND("Resource Not found"),
    METHOD_NOT_ALLOWED("Method Not Allowed"),
    BAD_REQUEST("Invalid parameter"),
	CONFLICT("Resource already exists"),
	NO_CONTENT("No Content");
	
    private final String msg;
}
