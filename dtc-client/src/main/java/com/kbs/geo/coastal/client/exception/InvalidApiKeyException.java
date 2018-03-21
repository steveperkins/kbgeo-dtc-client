package com.kbs.geo.coastal.client.exception;

public class InvalidApiKeyException extends ApiInputException {

	private static final long serialVersionUID = 1L;

	public InvalidApiKeyException() {
		super();
	}

	public InvalidApiKeyException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidApiKeyException(String message) {
		super(message);
	}

	public InvalidApiKeyException(Throwable cause) {
		super(cause);
	}
	
}
