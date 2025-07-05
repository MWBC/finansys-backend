package com.finansys.backend.exception;

public class BadRequestException extends RuntimeException {
	
    private static final long serialVersionUID = 2762842513902827259L;

	public BadRequestException(String message) {
		
        super(message);
    }
}
