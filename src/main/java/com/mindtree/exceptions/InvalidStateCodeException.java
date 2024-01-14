package com.mindtree.exceptions;

public class InvalidStateCodeException extends Exception{

	private final String message;
	public InvalidStateCodeException(String message) {
		super(message);
		this.message = message;
		
	}
	
	@Override
	public String getMessage() {
		return this.message;
	}
}
