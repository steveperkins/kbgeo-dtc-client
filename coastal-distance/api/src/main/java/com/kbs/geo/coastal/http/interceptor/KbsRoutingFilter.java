package com.kbs.geo.coastal.http.interceptor;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;
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

import com.kbs.geo.coastal.http.exception.InvalidSecurityTokenException;

/**
 * Handles request routing. This is a replacement for filter configuration in web.xml, as we need more complex path expressions than web.xml can handle.
 * @author Steve
 *
 */
@Component
public class KbsRoutingFilter extends GenericFilterBean {
	private static final Logger LOG = LoggerFactory.getLogger(KbsRoutingFilter.class);
	private static final String CONSOLE_AUTH_PATH = "/api/console/auth";
	private static final String CONSOLE_VERIFY_TOKEN_PATH = "/api/console/verify-token";
	private static final String CONSOLE_STARTSWITH_PATH = "/api/console";
	
	@Autowired
	private KbsApiWebClientAuthFilter webClientAuthFilter;	// /console/auth
	
	@Autowired
	private KbsApiWebClientSecurityFilter webClientSecurityFilter; // /console/verify-token, /console/*
	
	@Autowired
	private KbsApiClientSecurityFilter apiClientSecurityFilter; // /*
	
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
		
		// If this is a simple CORS preflight request, short-circuit and return
		setCommonCorsHeaders(httpServletRequest, httpServletResponse);
		if("OPTIONS".equals((httpServletRequest).getMethod())) {
			if(doCorsPreflight(httpServletRequest, httpServletResponse)) {
				httpServletResponse.setStatus(200);
			} else {
				httpServletResponse.setStatus(403);
			}
			chain.doFilter(servletRequest, servletResponse);
			return;
		}
		
		
		String path = httpServletRequest.getRequestURI();
		if(path.equals(CONSOLE_AUTH_PATH)) {
			webClientAuthFilter.doFilter(httpServletRequest, httpServletResponse, chain);
		} else if(path.equals(CONSOLE_VERIFY_TOKEN_PATH)) {
			webClientSecurityFilter.doFilter(httpServletRequest, httpServletResponse, chain);
		} else if(path.startsWith(CONSOLE_STARTSWITH_PATH)) {
			webClientSecurityFilter.doFilter(httpServletRequest, httpServletResponse, chain);
		} else {
			apiClientSecurityFilter.doFilter(httpServletRequest, httpServletResponse, chain);
		}
		
	}
	
	protected void setCommonCorsHeaders(HttpServletRequest request, HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin",  "*"); // Allow from all domains
		/*String origin = request.getHeader("Origin");
		LOG.info("Request received, Origin is {}", origin);
		if(StringUtils.isNotBlank(origin)) {
			// Strip off the trailing slash because CORS is a fucking joke
			if(origin.endsWith("/")) origin = origin.substring(0, origin.length() - 1);
			response.addHeader("Access-Control-Allow-Origin",  origin); // Allow from all domains
		} else {
			response.addHeader("Access-Control-Allow-Origin",  (StringUtils.isNotBlank(httpCorsAccessControlAllowOrigin) ? httpCorsAccessControlAllowOrigin : "*"));
		}*/
		String accessControlRequestHeaders = request.getHeader("Access-Control-Request-Headers");
		if(StringUtils.isBlank(accessControlRequestHeaders)) accessControlRequestHeaders = httpCorsAccessControlAllowHeaders; 
		response.addHeader("Access-Control-Allow-Headers",  accessControlRequestHeaders);
		
		String accessControlRequestMethod = request.getHeader("Access-Control-Request-Method");
		if(StringUtils.isBlank(accessControlRequestMethod)) accessControlRequestMethod = StringUtils.join(validRequestTypes); 
		response.addHeader("Access-Control-Allow-Methods", accessControlRequestMethod);
	}
	
	/**
	 * Does CORS junk
	 * @param request
	 * @param response
	 * @return TRUE if the CORS headers are valid, FALSE otherwise
	 */
	protected Boolean doCorsPreflight(HttpServletRequest request, HttpServletResponse response) {
		// See http://www.html5rocks.com/static/images/cors_server_flowchart.png for flow
		Boolean isValid = Boolean.FALSE;
		response.addHeader("Access-Control-Allow-Credentials",  (StringUtils.isNotBlank(httpCorsAccessControlAllowCredentials) ? httpCorsAccessControlAllowCredentials : "false"));
		isValid = Boolean.TRUE;
		
		return isValid;
		
	}
	private static final List<String> validRequestTypes = Arrays.asList(new String[] {"OPTIONS", "GET", "POST", "PUT", "DELETE", "TRACE", "PATCH", "HEAD"}); 

}
