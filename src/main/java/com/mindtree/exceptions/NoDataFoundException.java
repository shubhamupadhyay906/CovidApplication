package com.mindtree.exceptions;

public class NoDataFoundException extends Exception{

	private final String message;

	public NoDataFoundException(String message) {
		super(message);
		this.message = message;
	}
	
	@Override
	public String getMessage() {
		return this.message;
	}
}
