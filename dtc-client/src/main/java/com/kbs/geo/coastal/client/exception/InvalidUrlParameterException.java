package com.kbs.geo.coastal.client.exception;

public class InvalidUrlParameterException extends ApiInputException {

	private static final long serialVersionUID = 1L;

	public InvalidUrlParameterException() {
		super();
	}

	public InvalidUrlParameterException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidUrlParameterException(String message) {
		super(message);
	}

	public InvalidUrlParameterException(Throwable cause) {
		super(cause);
	}
	
}
