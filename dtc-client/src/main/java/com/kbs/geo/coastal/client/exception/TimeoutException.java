package com.kbs.geo.coastal.client.exception;

public class TimeoutException extends ApiClientException {

	private static final long serialVersionUID = 1L;

	public TimeoutException() {
		super();
	}

	public TimeoutException(String message, Throwable cause) {
		super(message, cause);
	}

	public TimeoutException(String message) {
		super(message);
	}

	public TimeoutException(Throwable cause) {
		super(cause);
	}
	
}
