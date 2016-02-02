package com.kbs.geo.coastal.http.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = HttpErrors.SECURITY_INVALID_CREDENTIALS_MESSAGE)
public class InvalidCredentialsException extends HttpUnauthorizedException {
	private static final long serialVersionUID = 1L;

	public InvalidCredentialsException() {
		super(HttpErrors.SECURITY_INVALID_CREDENTIALS_MESSAGE);
	}

	public InvalidCredentialsException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public InvalidCredentialsException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidCredentialsException(String message) {
		super(message);
	}

	public InvalidCredentialsException(Throwable cause) {
		super(HttpErrors.SECURITY_INVALID_CREDENTIALS_MESSAGE, cause);
	}
	
}