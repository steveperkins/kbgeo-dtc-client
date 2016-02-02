package com.kbs.geo.coastal.http.interceptor;

import java.io.IOException;
import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.kbs.geo.coastal.http.exception.ExpiredCredentialsException;
import com.kbs.geo.coastal.http.exception.InvalidCredentialsException;
import com.kbs.geo.coastal.http.exception.InvalidSecurityTokenException;
import com.kbs.geo.coastal.http.exception.KbsRestException;
import com.kbs.geo.coastal.http.model.HttpErrorModel;
import com.kbs.geo.coastal.http.util.MediaTypeHelper;
import com.kbs.geo.coastal.model.WebClientAuthResponse;
import com.kbs.geo.coastal.model.billing.ClientAuthWeb;
import com.kbs.geo.coastal.service.ClientAuthWebService;
import com.kbs.geo.coastal.service.RequestErrorService;
import com.kbs.geo.http.security.JsonWebTokenUtil;

/**
 * Pass-through filter for /auth endpoints
 * @author Steve
 *
 */
@Component
public class KbsApiWebClientAuthFilter extends GenericFilterBean {
	private static final Logger LOG = LoggerFactory.getLogger(KbsApiWebClientAuthFilter.class);
	
	@Autowired
	private ClientAuthWebService clientAuthService;
	
	@Autowired
	private JsonWebTokenUtil jsonWebTokenUtil;
	
	@Autowired
	private RequestErrorService requestErrorService;
	
	@Resource(name="http.cors.access.control.allow.credentials")
	private String httpCorsAccessControlAllowCredentials;
	
	@Resource(name="http.cors.access.control.allow.origin")
	private String httpCorsAccessControlAllowOrigin;
	
	@Resource(name="http.cors.access.control.allow.headers")
	private String httpCorsAccessControlAllowHeaders;
	
	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException, InvalidSecurityTokenException {
		HttpServletRequest httpServletRequest = (HttpServletRequest)servletRequest;
		HttpServletResponse httpServletResponse = (HttpServletResponse)servletResponse;
		
		try {
			String username = httpServletRequest.getHeader(HeaderConstants.CONSOLE_USER_NAME);
			String base64EncodedPassword = httpServletRequest.getHeader(HeaderConstants.CONSOLE_USER_PASSWORD);
			
			ClientAuthWeb clientAuth = clientAuthService.getByUsernamePassword(username, base64EncodedPassword);
			if(null == clientAuth) throw new InvalidCredentialsException();
			if(null != clientAuth.getExpires() && new Date().before(clientAuth.getExpires())) throw new ExpiredCredentialsException();
			
			WebClientAuthResponse authResponse = new WebClientAuthResponse();
			authResponse.setKbWebToken(jsonWebTokenUtil.createToken(String.valueOf(clientAuth.getId()), clientAuth.getClientAuth().getToken(), clientAuth.getUsername()));
			authResponse.setKbUserRole("user");
			
			httpServletResponse.addHeader(HeaderConstants.CONSOLE_SECURITY_TOKEN, authResponse.getKbWebToken());
			httpServletResponse.addHeader(HeaderConstants.CONSOLE_USER_ROLE, authResponse.getKbUserRole());
			LOG.info("Web client credentials are OK");
			
			String accept = httpServletRequest.getHeader("Accept");
			httpServletResponse.setContentType(MediaTypeHelper.getFirstValidType(accept));
			httpServletResponse.getOutputStream().print(new Gson().toJson(authResponse));
			
			LOG.info("Web client authentication request completed successfully");
			return;
		} catch(Exception e) {
			LOG.error("Could not complete console request", e);
			respondWithError(httpServletRequest, httpServletResponse, e);
		}
	}
	
	
	private void respondWithError(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Throwable e) throws JsonGenerationException, JsonMappingException, IOException {
		int status = 401;
		if(e instanceof KbsRestException) {
			status = ((KbsRestException)e).getHttpStatus().value();
		}
		httpServletResponse.setStatus(status);
		HttpErrorModel errorModel = new HttpErrorModel(e.getMessage());
		ObjectMapper mapper = new ObjectMapper(); 
		mapper.writeValue(httpServletResponse.getOutputStream(), errorModel);
	}
}
