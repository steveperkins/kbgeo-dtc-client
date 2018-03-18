package com.kbs.geo.coastal.http.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = HttpErrors.ERROR_ACCESS_VIOLATION)
public class HttpUnauthorizedException extends KbsRestException {
	private static final long serialVersionUID = 1L;
	public HttpUnauthorizedException() {
		super(HttpErrors.ERROR_ACCESS_VIOLATION);
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