package com.kbs.geo.coastal.http.exception;

public class HttpErrors {
	public static final String SECURITY_TOKEN_MISSING_MESSAGE = "No authentication token provided";
	public static final String SECURITY_TOKEN_INVALID_MESSAGE = "The provided authentication token is invalid";
	public static final String SECURITY_TOKEN_EXPIRED_MESSAGE = "The provided authentication token has expired";
	public static final String SECURITY_IP_INVALID_MESSAGE = "Your IP is not authorized to use this service";
	public static final String SECURITY_REFERER_INVALID_MESSAGE = "The originating domain is not authorized to use this service";
	public static final String SECURITY_INVALID_CREDENTIALS_MESSAGE = "Invalid credentials";
	public static final String SECURITY_EXPIRED_CREDENTIALS_MESSAGE = "Expired credentials";
	
	public static final String BILLING_REQUEST_COUNT_EXCEEDED_MESSAGE = "Your contract's maximum request count has been reached";
	public static final String BILLING_SERVICE_NOT_ALLOCATED_MESSAGE = "No contract exists for the requested service. Please contact KBS Systems (help@kbgeo.com) to have this service allocated to your account";
	public static final String ERROR_NOT_FOUND = "The requested resource does not exist";
	public static final String ERROR_ACCESS_VIOLATION = "Your account does not have access to this resource. Please contact KBS Systems  (help@kbgeo.com).";
	public static final String ERROR_UNKNOWN_MESSAGE = "Server error";
	
	public static final String VALIDATION_INVALID_ADDRESS = "Invalid address";
	public static final String VALIDATION_INVALID_COORDINATES = "Invalid latitude/longitude pair";
	public static final String NO_MATCH_FOUND= "No match found";
}
