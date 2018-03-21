package com.kbs.geo.coastal.client.exception;

public class MalformedRequestException extends ApiInputException {

	private static final long serialVersionUID = 1L;

	public MalformedRequestException() {
		super();
	}

	public MalformedRequestException(String message, Throwable cause) {
		super(message, cause);
	}

	public MalformedRequestException(String message) {
		super(message);
	}

	public MalformedRequestException(Throwable cause) {
		super(cause);
	}
	
}
