package com.kbs.geo.coastal.http.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = HttpErrors.SECURITY_EXPIRED_CREDENTIALS_MESSAGE)
public class ExpiredCredentialsException extends HttpUnauthorizedException {
	private static final long serialVersionUID = 1L;

	public ExpiredCredentialsException() {
		super();
	}

	public ExpiredCredentialsException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ExpiredCredentialsException(String message, Throwable cause) {
		super(message, cause);
	}

	public ExpiredCredentialsException(String message) {
		super(message);
	}

	public ExpiredCredentialsException(Throwable cause) {
		super(cause);
	}
	
}