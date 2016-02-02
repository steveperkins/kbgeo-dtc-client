package com.kbs.geo.http.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.security.Key;
import java.util.Date;

import javax.annotation.Resource;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.kbs.geo.coastal.http.exception.ExpiredSecurityTokenException;
import com.kbs.geo.coastal.http.exception.InvalidSecurityTokenException;

@Service
public class JsonWebTokenUtilImpl implements JsonWebTokenUtil {
	private static final Logger LOG = LoggerFactory.getLogger(JsonWebTokenUtilImpl.class);
	
	@Resource(name="jwt.secret")
	private String jwtSecret;
	
	@Resource(name="jwt.ttl.ms")
	private Integer jwtTtlMs;
	
	@Resource(name="jwt.issuer")
	private String jwtIssuer;
	
	public String createToken(String id, String subject) {
/*		// The JWT signature algorithm we will be using to sign the token
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

		long nowMillis = System.currentTimeMillis();
		Date now = new Date(nowMillis);

		// We will sign our JWT with our ApiKey secret
		byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(jwtSecret);
		Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

		// Let's set the JWT Claims
		JwtBuilder builder = Jwts.builder()
				.setId(id)
				.setIssuedAt(now)
				.setSubject(subject)
				.setIssuer(jwtIssuer)
				.signWith(signatureAlgorithm, signingKey);

		// if it has been specified, let's add the expiration
		if (jwtTtlMs>= 0) {
			long expMillis = nowMillis + jwtTtlMs;
			Date exp = new Date(expMillis);
			builder.setExpiration(exp);
		}

		// Builds the JWT and serializes it to a compact, URL-safe string
		return builder.compact();*/
		return createToken(id, subject, null);
	}
	
	public String createToken(String id, String subject, String audience) {
		// The JWT signature algorithm we will be using to sign the token
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

		long nowMillis = System.currentTimeMillis();
		Date now = new Date(nowMillis);

		// We will sign our JWT with our ApiKey secret
		byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(jwtSecret);
		Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

		// Let's set the JWT Claims
		JwtBuilder builder = Jwts.builder()
				.setId(id)
				.setIssuedAt(now)
				.setSubject(subject)
				.setAudience(audience)
				.setIssuer(jwtIssuer)
				.signWith(signatureAlgorithm, signingKey);

		// if it has been specified, let's add the expiration
		if (jwtTtlMs>= 0) {
			long expMillis = nowMillis + jwtTtlMs;
			Date exp = new Date(expMillis);
			builder.setExpiration(exp);
		}

		// Builds the JWT and serializes it to a compact, URL-safe string
		return builder.compact();
	}
	
	public Claims parseJWT(String jwt) {
		//This line will throw an exception if it is not a signed JWS (as expected)
		Claims claims = Jwts.parser()         
		   .setSigningKey(DatatypeConverter.parseBase64Binary(jwtSecret))
		   .parseClaimsJws(jwt)
		   .getBody();
		
		if(null == claims) {
			// Bad token provided
			LOG.error("Bad JWT token! No claims found in decrypted token.");
			throw new InvalidSecurityTokenException();
		}
		
		if(null == claims.getExpiration()) {
			// Bad token provided
			LOG.error("Bad JWT token! No expiration date specified.");
			throw new InvalidSecurityTokenException();
		}
		
		if(null == claims.getExpiration()) {
			// Bad token provided
			LOG.error("Bad JWT token! No expiration date specified.");
			throw new InvalidSecurityTokenException();
		}
		
		if(new Date().after(claims.getExpiration())) {
			// Expired token provided
			LOG.error("JWT token expired!");
			throw new ExpiredSecurityTokenException();
		}
		
		if(!issuerIsValid(claims.getIssuer())) {
			// Bad token provided
			LOG.error(String.format("Invalid JWT token! Invalid issuer '%s'", claims.getIssuer()));
			throw new InvalidSecurityTokenException();
		}
		
		if(StringUtils.isBlank(claims.getSubject())) {
			// Bad token provided
			LOG.error("Invalid JWT token! No token provided.");
			throw new InvalidSecurityTokenException();
		}
		
		System.out.println("ID: " + claims.getId());
		System.out.println("Subject: " + claims.getSubject());
		System.out.println("Issuer: " + claims.getIssuer());
		System.out.println("Expiration: " + claims.getExpiration());
		return claims;
	}
	
	public Boolean issuerIsValid(String issuer) {
		return (StringUtils.isNotBlank(issuer) && issuer.equals(jwtIssuer));
	}
}
