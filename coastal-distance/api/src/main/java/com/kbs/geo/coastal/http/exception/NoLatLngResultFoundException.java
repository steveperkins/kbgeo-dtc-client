package com.kbs.geo.coastal.http.exception;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(value=HttpStatus.NOT_FOUND, reason=HttpErrors.NO_MATCH_FOUND)
public class NoLatLngResultFoundException extends HttpNotFoundException {
	private static final long serialVersionUID = 1L;

	public NoLatLngResultFoundException() {
		super(HttpErrors.NO_MATCH_FOUND);
	}

	public NoLatLngResultFoundException(String arg0, Throwable arg1, boolean arg2,
			boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	public NoLatLngResultFoundException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public NoLatLngResultFoundException(String arg0) {
		super(arg0);
	}

	public NoLatLngResultFoundException(Throwable arg0) {
		super(arg0);
	}
	
}