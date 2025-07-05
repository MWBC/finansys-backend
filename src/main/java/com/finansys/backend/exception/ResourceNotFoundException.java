package com.finansys.backend.exception;

public class ResourceNotFoundException extends RuntimeException {
	
    private static final long serialVersionUID = -7021291783001693828L;

	public ResourceNotFoundException(String message) {
    	
        super(message);
    }
}