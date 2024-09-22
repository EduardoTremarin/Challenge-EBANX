package com.ebanx.challenge.exceptions;

public class AccountAlreadyExistsException extends RuntimeException{
	
	private static final long serialVersionUID = 1L;

	public AccountAlreadyExistsException(String msg) {
        super(msg);
    }

    public AccountAlreadyExistsException(String msg, Throwable cause) {
        super(msg, cause);
    }
	
}
