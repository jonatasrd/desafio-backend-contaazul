package com.contaazul.bankslip.exception;

import com.contaazul.bankslip.exception.model.MessageError;

public class ResourceAlreadyExists extends RuntimeException  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8567371279651782029L;

	public ResourceAlreadyExists() {
        super(MessageError.CONFLICT.getMsg());
    }

    public ResourceAlreadyExists(String message) {
        super(message);
    }
}
