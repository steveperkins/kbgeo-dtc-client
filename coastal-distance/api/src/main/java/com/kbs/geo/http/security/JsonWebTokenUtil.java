package com.kbs.geo.http.security;

import io.jsonwebtoken.Claims;

public interface JsonWebTokenUtil {
	
	public String createToken(String id, String subject);
	
	public String createToken(String id, String subject, String audience);
	
	public Claims parseJWT(String jwt);
	
	public Boolean issuerIsValid(String issuer);
}
