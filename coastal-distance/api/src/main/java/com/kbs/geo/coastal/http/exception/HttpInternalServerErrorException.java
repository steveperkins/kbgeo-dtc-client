package com.kbs.geo.coastal.http.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = HttpErrors.ERROR_UNKNOWN_MESSAGE)
public class HttpInternalServerErrorException extends KbsRestException {
	private static final long serialVersionUID = 1L;
	public HttpInternalServerErrorException() {
		super(HttpErrors.ERROR_UNKNOWN_MESSAGE);
	}

	public HttpInternalServerErrorException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public HttpInternalServerErrorException(String message, Throwable cause) {
		super(message, cause);
	}

	public HttpInternalServerErrorException(String message) {
		super(message);
	}

	public HttpInternalServerErrorException(Throwable cause) {
		super(HttpErrors.ERROR_UNKNOWN_MESSAGE, cause);
	}

	@Override
	public HttpStatus getHttpStatus() {
		return HttpStatus.INTERNAL_SERVER_ERROR;
	}
}