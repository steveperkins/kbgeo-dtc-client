package com.kbs.geo.coastal.http.exception;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(value=HttpStatus.BAD_REQUEST, reason="Invalid latitude/longitude pair")
public class InvalidLatLngException extends HttpBadRequestException {
	private static final long serialVersionUID = 1L;

	public InvalidLatLngException() {
		super();
	}

	public InvalidLatLngException(String arg0, Throwable arg1, boolean arg2,
			boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	public InvalidLatLngException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public InvalidLatLngException(String arg0) {
		super(arg0);
	}

	public InvalidLatLngException(Throwable arg0) {
		super(arg0);
	}
	
}