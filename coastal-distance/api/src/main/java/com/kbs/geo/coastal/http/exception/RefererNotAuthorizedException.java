package com.kbs.geo.coastal.http.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = HttpErrors.SECURITY_REFERER_INVALID_MESSAGE)
public class RefererNotAuthorizedException extends HttpUnauthorizedException {
	private static final long serialVersionUID = 1L;

	public RefererNotAuthorizedException() {
		super();
	}

	public RefererNotAuthorizedException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public RefererNotAuthorizedException(String message, Throwable cause) {
		super(message, cause);
	}

	public RefererNotAuthorizedException(String message) {
		super(message);
	}

	public RefererNotAuthorizedException(Throwable cause) {
		super(cause);
	}
	
}