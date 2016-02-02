package com.kbs.geo.coastal.http.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = HttpErrors.SECURITY_IP_INVALID_MESSAGE)
public class IpNotAuthorizedException extends HttpUnauthorizedException {
	private static final long serialVersionUID = 1L;

	public IpNotAuthorizedException() {
		super();
	}

	public IpNotAuthorizedException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public IpNotAuthorizedException(String message, Throwable cause) {
		super(message, cause);
	}

	public IpNotAuthorizedException(String message) {
		super(message);
	}

	public IpNotAuthorizedException(Throwable cause) {
		super(cause);
	}
	
}