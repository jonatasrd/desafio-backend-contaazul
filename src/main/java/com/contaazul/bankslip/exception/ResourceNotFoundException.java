package com.contaazul.bankslip.exception;

import com.contaazul.bankslip.exception.model.MessageError;

public class ResourceNotFoundException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = -5427746055556631488L;

	public ResourceNotFoundException() {
        super(MessageError.RESOURCE_NOT_FOUND.getMsg());
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
