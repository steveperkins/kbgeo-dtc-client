package com.kbs.geo.coastal.http;

import io.jsonwebtoken.Claims;

import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.kbs.geo.coastal.http.exception.ExpiredCredentialsException;
import com.kbs.geo.coastal.http.exception.InvalidCredentialsException;
import com.kbs.geo.coastal.http.interceptor.HeaderConstants;
import com.kbs.geo.coastal.model.WebClientAuthResponse;
import com.kbs.geo.coastal.model.billing.ClientAuth;
import com.kbs.geo.coastal.model.billing.ClientAuthWeb;
import com.kbs.geo.coastal.service.ClientAuthService;
import com.kbs.geo.coastal.service.ClientAuthWebService;
import com.kbs.geo.http.security.JsonWebTokenUtil;

@RestController
public class ConsoleAuthController {
	private static final Logger LOG = LoggerFactory.getLogger(ConsoleAuthController.class);
	
	@Autowired
	private ClientAuthWebService clientAuthWebService;
	
	@Autowired
	private ClientAuthService clientAuthService; 
	
	@Autowired
	private JsonWebTokenUtil jsonWebTokenUtil;
	
	@RequestMapping(value="console/auth", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String authenticateUser(
    		@RequestHeader(value = HeaderConstants.CONSOLE_USER_NAME) String username,
    		@RequestHeader(value = HeaderConstants.CONSOLE_USER_PASSWORD) String base64EncodedPassword,
    		HttpServletResponse response) {
		LOG.info("GET /console/auth for user {}", username);
		ClientAuthWeb clientAuth = clientAuthWebService.getByUsernamePassword(username, base64EncodedPassword);
		if(null == clientAuth) throw new InvalidCredentialsException();
		if(null != clientAuth.getExpires() && new Date().before(clientAuth.getExpires())) throw new ExpiredCredentialsException();
		
		response.addHeader(HeaderConstants.CONSOLE_SECURITY_TOKEN, jsonWebTokenUtil.createToken(String.valueOf(clientAuth.getId()), clientAuth.getClientAuth().getToken(), clientAuth.getUsername()));
		response.addHeader(HeaderConstants.CONSOLE_USER_ROLE, "user");
		return "{\"status\": \"success\"}";
    }
	
	@RequestMapping(value="console/verify-token", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody WebClientAuthResponse verifyJwtToken (
    		@RequestHeader(value = HeaderConstants.CONSOLE_SECURITY_TOKEN) String jwtToken,
    		HttpServletResponse response) {
		LOG.info("GET /console/verify-token for token {}", jwtToken);
		Claims jwtClaims = jsonWebTokenUtil.parseJWT(jwtToken);
		if(null == jwtClaims) throw new InvalidCredentialsException("Not a valid web token");
		
		String authToken = jwtClaims.getSubject();
		if(StringUtils.isBlank(authToken)) throw new InvalidCredentialsException("Not a valid auth token");
		ClientAuth clientAuth = clientAuthService.getByToken(authToken);
		if(null == clientAuth) throw new InvalidCredentialsException();
		if(null != clientAuth.getExpires() && new Date().before(clientAuth.getExpires())) throw new ExpiredCredentialsException();
		
		response.addHeader(HeaderConstants.CONSOLE_SECURITY_TOKEN, jwtToken);
		response.addHeader(HeaderConstants.CONSOLE_USER_ROLE, "user");
		
		WebClientAuthResponse verifyTokenResponse = new WebClientAuthResponse();
		verifyTokenResponse.setKbWebToken(jwtToken);
		verifyTokenResponse.setKbUserRole("user");
		
		return verifyTokenResponse;
    }
	
	@RequestMapping(value="console/user-info", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String getUserInfo (
    		@RequestHeader(value = HeaderConstants.CONSOLE_SECURITY_TOKEN) String jwtToken,
    		HttpServletResponse response) {
		LOG.info("GET /console/user-info for token {}", jwtToken);
		Claims jwtClaims = jsonWebTokenUtil.parseJWT(jwtToken);
		if(null == jwtClaims) throw new InvalidCredentialsException("Not a valid web token");
		
		String authToken = jwtClaims.getSubject();
		if(StringUtils.isBlank(authToken)) throw new InvalidCredentialsException("Not a valid auth token");
		
		String userId = jwtClaims.getAudience();
		
		response.addHeader(HeaderConstants.CONSOLE_SECURITY_TOKEN, jwtToken);
		response.addHeader(HeaderConstants.CONSOLE_USER_ROLE, "user");
		return "{\"userId\": \"" + userId + "\"}";
    }
	
}
