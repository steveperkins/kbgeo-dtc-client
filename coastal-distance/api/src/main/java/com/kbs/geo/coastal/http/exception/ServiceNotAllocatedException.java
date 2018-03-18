package com.kbs.geo.coastal.http.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = HttpErrors.BILLING_SERVICE_NOT_ALLOCATED_MESSAGE)
public class ServiceNotAllocatedException extends HttpUnauthorizedException {
	private static final long serialVersionUID = 1L;

	public ServiceNotAllocatedException() {
		super(HttpErrors.BILLING_SERVICE_NOT_ALLOCATED_MESSAGE);
	}

	public ServiceNotAllocatedException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ServiceNotAllocatedException(String message, Throwable cause) {
		super(message, cause);
	}

	public ServiceNotAllocatedException(String message) {
		super(message);
	}

	public ServiceNotAllocatedException(Throwable cause) {
		super(cause);
	}
	
}