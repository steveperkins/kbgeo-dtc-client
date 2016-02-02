package com.kbs.geo.coastal.http.exception;

import org.springframework.http.HttpStatus;


public class HttpUnauthorizedException extends KbsRestException {
	private static final long serialVersionUID = 1L;
	public HttpUnauthorizedException() {
		super();
	}

	public HttpUnauthorizedException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public HttpUnauthorizedException(String message, Throwable cause) {
		super(message, cause);
	}

	public HttpUnauthorizedException(String message) {
		super(message);
	}

	public HttpUnauthorizedException(Throwable cause) {
		super(cause);
	}

	@Override
	public HttpStatus getHttpStatus() {
		return HttpStatus.UNAUTHORIZED;
	}
}