package com.kbs.geo.coastal.client.exception;

public class ApiUrlNotFoundException extends ApiInputException {

	private static final long serialVersionUID = 1L;

	public ApiUrlNotFoundException() {
		super();
	}

	public ApiUrlNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public ApiUrlNotFoundException(String message) {
		super(message);
	}

	public ApiUrlNotFoundException(Throwable cause) {
		super(cause);
	}
	
}
