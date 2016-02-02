package com.kbs.geo.coastal.http.exception;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(value=HttpStatus.BAD_REQUEST, reason="Invalid address")
public class InvalidAddressException extends HttpBadRequestException {
	private static final long serialVersionUID = 1L;

	public InvalidAddressException() {
		super();
	}

	public InvalidAddressException(String arg0, Throwable arg1, boolean arg2,
			boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	public InvalidAddressException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public InvalidAddressException(String arg0) {
		super(arg0);
	}

	public InvalidAddressException(Throwable arg0) {
		super(arg0);
	}
	
}