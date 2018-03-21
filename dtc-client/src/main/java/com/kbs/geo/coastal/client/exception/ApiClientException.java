package com.kbs.geo.coastal.client.exception;

/**
 * ApiClientException subclasses are thrown when a request generates a server-side error. ApiInputExceptions are thrown when user input causes an error.
 * @author http://www.kbgeo.com
 *
 */
public abstract class ApiClientException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ApiClientException() {
		super();
	}

	public ApiClientException(String message, Throwable cause) {
		super(message, cause);
	}

	public ApiClientException(String message) {
		super(message);
	}

	public ApiClientException(Throwable cause) {
		super(cause);
	}
	
}
