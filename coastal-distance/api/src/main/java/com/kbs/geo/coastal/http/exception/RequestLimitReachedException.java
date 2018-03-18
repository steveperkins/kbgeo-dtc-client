package com.kbs.geo.coastal.http.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = HttpErrors.BILLING_REQUEST_COUNT_EXCEEDED_MESSAGE)
public class RequestLimitReachedException extends HttpUnauthorizedException {
	private static final long serialVersionUID = 1L;

	public RequestLimitReachedException() {
		super(HttpErrors.BILLING_REQUEST_COUNT_EXCEEDED_MESSAGE);
	}

	public RequestLimitReachedException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public RequestLimitReachedException(String message, Throwable cause) {
		super(message, cause);
	}

	public RequestLimitReachedException(String message) {
		super(message);
	}

	public RequestLimitReachedException(Throwable cause) {
		super(cause);
	}
	
}