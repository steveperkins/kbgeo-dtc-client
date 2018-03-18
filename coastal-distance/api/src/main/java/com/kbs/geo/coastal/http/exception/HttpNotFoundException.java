package com.kbs.geo.coastal.http.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = HttpErrors.ERROR_NOT_FOUND)
public class HttpNotFoundException extends KbsRestException {
	private static final long serialVersionUID = 1L;
	public HttpNotFoundException() {
		super(HttpErrors.ERROR_NOT_FOUND);
	}

	public HttpNotFoundException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public HttpNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public HttpNotFoundException(String message) {
		super(message);
	}

	public HttpNotFoundException(Throwable cause) {
		super(cause);
	}

	@Override
	public HttpStatus getHttpStatus() {
		return HttpStatus.NOT_FOUND;
	}
}