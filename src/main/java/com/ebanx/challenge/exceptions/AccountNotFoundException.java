package com.ebanx.challenge.exceptions;

public class AccountNotFoundException extends RuntimeException{
	
	private static final long serialVersionUID = 1L;

	public AccountNotFoundException(String msg) {
        super(msg);
    }

    public AccountNotFoundException(String msg, Throwable cause) {
        super(msg, cause);
    }
	
}
