package com.kbs.geo.coastal.http.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Bad request")
public class HttpBadRequestException extends KbsRestException {
	private static final long serialVersionUID = 1L;
	public HttpBadRequestException() {
		super("Bad request");
	}

	public HttpBadRequestException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public HttpBadRequestException(String message, Throwable cause) {
		super(message, cause);
	}

	public HttpBadRequestException(String message) {
		super(message);
	}

	public HttpBadRequestException(Throwable cause) {
		super(cause);
	}

	@Override
	public HttpStatus getHttpStatus() {
		return HttpStatus.BAD_REQUEST;
	}
}