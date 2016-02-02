package com.kbs.geo.coastal.http.exception;

import org.springframework.http.HttpStatus;


public class KbsRestException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private HttpStatus httpStatus;
	private Integer clientId;
	public KbsRestException() {
		super();
	}

	public KbsRestException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public KbsRestException(String message, Throwable cause) {
		super(message, cause);
	}

	public KbsRestException(String message) {
		super(message);
	}

	public KbsRestException(Throwable cause) {
		super(cause);
	}

	public KbsRestException(String message, Throwable cause, Integer clientId) {
		super(message, cause);
		this.clientId = clientId;
	}
	
	public HttpStatus getHttpStatus() {
		return httpStatus;
	}

	protected void setHttpStatus(HttpStatus httpStatus) {
		this.httpStatus = httpStatus;
	}

	public Integer getClientId() {
		return clientId;
	}

	protected void setClientId(Integer clientId) {
		this.clientId = clientId;
	}
	
}