package com.kbs.geo.coastal.client.exception;

public class ServerException extends ApiClientException {

	private static final long serialVersionUID = 1L;

	public ServerException() {
		super();
	}

	public ServerException(String message, Throwable cause) {
		super(message, cause);
	}

	public ServerException(String message) {
		super(message);
	}

	public ServerException(Throwable cause) {
		super(cause);
	}
	
}
