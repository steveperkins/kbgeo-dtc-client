package com.kbs.geo.coastal.client.exception;

public abstract class ApiInputException extends IllegalArgumentException {

	private static final long serialVersionUID = 1L;

	public ApiInputException() {
		super();
	}

	public ApiInputException(String message, Throwable cause) {
		super(message, cause);
	}

	public ApiInputException(String message) {
		super(message);
	}

	public ApiInputException(Throwable cause) {
		super(cause);
	}
	
}
