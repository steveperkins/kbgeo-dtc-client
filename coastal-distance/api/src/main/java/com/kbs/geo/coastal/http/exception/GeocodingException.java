package com.kbs.geo.coastal.http.exception;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(value=HttpStatus.BAD_REQUEST, reason="Address could not be geocoded")
public class GeocodingException extends HttpBadRequestException {
	private static final long serialVersionUID = 1L;

	public GeocodingException() {
		super();
	}

	public GeocodingException(String arg0, Throwable arg1, boolean arg2,
			boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	public GeocodingException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public GeocodingException(String arg0) {
		super(arg0);
	}

	public GeocodingException(Throwable arg0) {
		super(arg0);
	}
	
}