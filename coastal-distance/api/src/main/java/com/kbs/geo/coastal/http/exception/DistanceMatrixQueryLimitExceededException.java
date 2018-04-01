package com.kbs.geo.coastal.http.exception;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(value=HttpStatus.BAD_REQUEST, reason="Distance matrix query limit exceeded")
public class DistanceMatrixQueryLimitExceededException extends DistanceMatrixException {
	private static final long serialVersionUID = 1L;

	public DistanceMatrixQueryLimitExceededException() {
		super("Distance matrix query limit exceeded");
	}

	public DistanceMatrixQueryLimitExceededException(String arg0, Throwable arg1, boolean arg2,
			boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	public DistanceMatrixQueryLimitExceededException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public DistanceMatrixQueryLimitExceededException(String arg0) {
		super(arg0);
	}

	public DistanceMatrixQueryLimitExceededException(Throwable arg0) {
		super(arg0);
	}
	
}