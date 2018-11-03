package com.contaazul.bankslip.exception;

import com.contaazul.bankslip.exception.model.MessageError;

public class InvalidStatusException extends RuntimeException {

	public InvalidStatusException() {
        super(MessageError.CONFLICT.getMsg());
    }

    public InvalidStatusException(String message) {
        super(message);
    }
}
