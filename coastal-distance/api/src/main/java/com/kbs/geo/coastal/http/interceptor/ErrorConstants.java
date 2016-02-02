package com.kbs.geo.coastal.http.interceptor;

public enum ErrorConstants {
	SECURITY_TOKEN_MISSING(401, "No authentication token provided"),
	SECURITY_TOKEN_INVALID(401, "The provided authentication token is invalid"),
	SECURITY_TOKEN_EXPIRED(401, "The provided authentication token has expired"),
	SECURITY_IP_INVALID(401, "Your IP is not authorized to use this service"),
	BILLING_REQUEST_COUNT_EXCEEDED(400, "Your contract's maximum request count has been reached"),
	BILLING_SERVICE_NOT_ALLOCATED(401, "No contract exists for the requested service. Please contact KBS GeoRisk to have this service allocated to your account"),
	ERROR_UNKNOWN(500, "Server error");
	
	private int httpCode;
	private String message;
	private ErrorConstants(int httpCode, String message) {
		this.httpCode = httpCode;
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}

	public int getHttpCode() {
		return httpCode;
	}
	
}
