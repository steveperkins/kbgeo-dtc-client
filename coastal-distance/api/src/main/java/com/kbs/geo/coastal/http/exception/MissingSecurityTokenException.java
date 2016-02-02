package com.kbs.geo.coastal.http.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = HttpErrors.SECURITY_TOKEN_MISSING_MESSAGE)
public class MissingSecurityTokenException extends HttpUnauthorizedException {
	private static final long serialVersionUID = 1L;

	public MissingSecurityTokenException() {
		super(HttpErrors.SECURITY_TOKEN_MISSING_MESSAGE);
	}

	public MissingSecurityTokenException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public MissingSecurityTokenException(String message, Throwable cause) {
		super(message, cause);
	}

	public MissingSecurityTokenException(String message) {
		super(message);
	}

	public MissingSecurityTokenException(Throwable cause) {
		super(HttpErrors.SECURITY_TOKEN_MISSING_MESSAGE, cause);
	}
	
}