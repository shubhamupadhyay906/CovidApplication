package com.mindtree.exceptions;

public class InvalidDateRangeException extends Exception{

	private final String message;

	public InvalidDateRangeException(String message) {
		super(message);
		this.message = message;
	}
	
	@Override
	public String getMessage() {
		return this.message;
	}
}
