package com.kbs.geo.coastal.client.exception;

public class NoApiKeySetException extends ApiInputException {

	private static final long serialVersionUID = 1L;

	public NoApiKeySetException() {
		super();
	}

	public NoApiKeySetException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoApiKeySetException(String message) {
		super(message);
	}

	public NoApiKeySetException(Throwable cause) {
		super(cause);
	}
	
}
