package com.kbs.geo.coastal.http.exception;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(value=HttpStatus.BAD_REQUEST, reason="No route found between addresses")
public class DistanceMatrixException extends HttpBadRequestException {
	private static final long serialVersionUID = 1L;

	public DistanceMatrixException() {
		super("No route found between addresses");
	}

	public DistanceMatrixException(String arg0, Throwable arg1, boolean arg2,
			boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	public DistanceMatrixException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public DistanceMatrixException(String arg0) {
		super(arg0);
	}

	public DistanceMatrixException(Throwable arg0) {
		super(arg0);
	}
	
}