package com.kbs.geo.coastal.http.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = HttpErrors.SECURITY_TOKEN_INVALID_MESSAGE)
public class InvalidSecurityTokenException extends HttpUnauthorizedException {
	private static final long serialVersionUID = 1L;

	public InvalidSecurityTokenException() {
		super(HttpErrors.SECURITY_TOKEN_INVALID_MESSAGE);
	}

	public InvalidSecurityTokenException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public InvalidSecurityTokenException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidSecurityTokenException(String message) {
		super(message);
	}

	public InvalidSecurityTokenException(Throwable cause) {
		super(HttpErrors.SECURITY_TOKEN_INVALID_MESSAGE, cause);
	}
	
}