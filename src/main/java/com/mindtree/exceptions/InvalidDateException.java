package com.mindtree.exceptions;

public class InvalidDateException extends Exception{

	private final String message;

	public InvalidDateException(String message) {
		super(message);
		this.message = message;
	}
	
	@Override
	public String getMessage() {
		return this.message;
	}
}
