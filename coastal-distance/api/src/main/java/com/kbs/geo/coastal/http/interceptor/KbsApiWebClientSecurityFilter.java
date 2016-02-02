package com.kbs.geo.coastal.http.interceptor;

import io.jsonwebtoken.Claims;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kbs.geo.coastal.http.exception.InvalidSecurityTokenException;
import com.kbs.geo.coastal.http.exception.KbsRestException;
import com.kbs.geo.coastal.http.model.HttpErrorModel;
import com.kbs.geo.coastal.model.billing.ClientAuth;
import com.kbs.geo.coastal.model.billing.ClientAuthWeb;
import com.kbs.geo.coastal.service.ClientAuthWebService;
import com.kbs.geo.coastal.service.ClientRequestService;
import com.kbs.geo.coastal.service.RequestErrorService;
import com.kbs.geo.http.security.JsonWebTokenUtil;
import com.kbs.geo.http.security.KbConsoleUserContext;

/**
 * Implements Json Web Token security for users of the KB Geo web console
 * @author Steve
 *
 */
@Component
public class KbsApiWebClientSecurityFilter extends GenericFilterBean {
	private static final Logger LOG = LoggerFactory.getLogger(KbsApiWebClientSecurityFilter.class);
	
	@Autowired
	private ApiSecurityService apiSecurityService;
	
	@Autowired
	private ClientRequestService clientRequestService;
	
	@Autowired
	private RequestErrorService requestErrorService;
	
	@Autowired
	private JsonWebTokenUtil jsonWebTokenUtil;
	
	@Autowired
	private ClientAuthWebService clientAuthWebService;
	
	@Autowired
	private KbConsoleUserContext kbContext;
	
	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest)servletRequest;
		HttpServletResponse httpServletResponse = (HttpServletResponse)servletResponse;
		try {
			LOG.info("Console request received");
			Claims claims = getClaims(httpServletRequest);
			
			String authToken = claims.getSubject();
			// Throws an exception if the request is not authenticated
			ClientAuth clientAuth = apiSecurityService.authenticateRequest(authToken, httpServletRequest, httpServletResponse);
			
			if(StringUtils.isNotBlank(claims.getAudience())) {
				ClientAuthWeb clientAuthWeb = clientAuthWebService.getByUsername(claims.getAudience());
				// We know the record must exist because the auth token was valid
				// Populate the current user bean so downstream Spring classes can benefit
				kbContext.setClientAuthWeb(clientAuthWeb);
			}
			chain.doFilter(httpServletRequest, httpServletResponse);
			LOG.info("Console request completed");
		} catch(Exception e) {
			LOG.error("Could not complete console request", e);
			respondWithError(httpServletRequest, httpServletResponse, e);
		}
	}
	
	private Claims getClaims(HttpServletRequest httpServletRequest) throws IOException, ServletException, InvalidSecurityTokenException {
		String jwtToken = httpServletRequest.getHeader(HeaderConstants.CONSOLE_SECURITY_TOKEN);
		if(StringUtils.isBlank(jwtToken)) {
			// No token provided
			LOG.error("Console request received but " + HeaderConstants.CONSOLE_SECURITY_TOKEN + " is blank/empty!");
			throw new InvalidSecurityTokenException();
		}
		
		return jsonWebTokenUtil.parseJWT(jwtToken);
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
