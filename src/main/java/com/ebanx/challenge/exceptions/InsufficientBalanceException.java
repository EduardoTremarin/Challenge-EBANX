package com.ebanx.challenge.exceptions;

public class InsufficientBalanceException extends RuntimeException{
	
	private static final long serialVersionUID = 1L;

	public InsufficientBalanceException(String msg) {
        super(msg);
    }

    public InsufficientBalanceException(String msg, Throwable cause) {
        super(msg, cause);
    }
	
}
