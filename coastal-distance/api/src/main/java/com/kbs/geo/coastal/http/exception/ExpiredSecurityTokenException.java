package com.kbs.geo.coastal.http.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = HttpErrors.SECURITY_TOKEN_EXPIRED_MESSAGE)
public class ExpiredSecurityTokenException extends HttpUnauthorizedException {
	private static final long serialVersionUID = 1L;

	public ExpiredSecurityTokenException() {
		super(HttpErrors.SECURITY_TOKEN_EXPIRED_MESSAGE);
	}

	public ExpiredSecurityTokenException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ExpiredSecurityTokenException(String message, Throwable cause) {
		super(message, cause);
	}

	public ExpiredSecurityTokenException(String message) {
		super(message);
	}

	public ExpiredSecurityTokenException(Throwable cause) {
		super(cause);
	}
	
}