package com.kbs.geo.coastal.http.interceptor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kbs.geo.coastal.http.exception.HttpInternalServerErrorException;
import com.kbs.geo.coastal.http.exception.InvalidSecurityTokenException;
import com.kbs.geo.coastal.http.exception.KbsRestException;
import com.kbs.geo.coastal.http.model.HttpErrorModel;
import com.kbs.geo.coastal.http.util.MediaTypeHelper;
import com.kbs.geo.coastal.model.billing.ClientAuth;
import com.kbs.geo.coastal.model.billing.ClientRequest;
import com.kbs.geo.coastal.model.billing.RequestError;
import com.kbs.geo.coastal.model.billing.RequestType;
import com.kbs.geo.coastal.service.ClientRequestService;
import com.kbs.geo.coastal.service.RequestErrorService;
import com.kbs.geo.http.security.KbApiAuthContext;

@Component
public class KbsApiClientSecurityFilter extends GenericFilterBean {
	private static final Logger LOG = Logger.getLogger(KbsApiClientSecurityFilter.class);

	@Autowired
	private ApiSecurityService apiSecurityService;
	
	@Autowired
	private ClientRequestService clientRequestService;
	
	@Autowired
	private RequestErrorService requestErrorService;
	
	@Autowired
	private KbApiAuthContext kbContext;
	
	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException, InvalidSecurityTokenException {
		HttpServletRequest httpServletRequest = (HttpServletRequest)servletRequest;
		HttpServletResponse httpServletResponse = (HttpServletResponse)servletResponse;
		
		ByteArrayOutputStream responseOutputStream = new ByteArrayOutputStream();
		BufferedHttpServletRequest bufferedRequest = new BufferedHttpServletRequest((HttpServletRequest)servletRequest);
		BufferedHttpServletResponse bufferedResponse = new BufferedHttpServletResponse((HttpServletResponse)servletResponse, responseOutputStream);
		Long clientId = null;
		try {
			String authToken = httpServletRequest.getHeader(HeaderConstants.API_SECURITY_TOKEN);
			
			// Throws an exception if the request is not authenticated
			ClientAuth clientAuth = apiSecurityService.authenticateRequest(authToken, httpServletRequest, httpServletResponse);
			// Make this authentication information available to downstream classes
			kbContext.setClientAuth(clientAuth);
			
			// Log request and response
			ClientRequest clientRequest = new ClientRequest();
			clientRequest.setClientAuthId(clientAuth.getId());
			clientRequest.setRequestBody(bufferedRequest.getBody());
			clientRequest.setRequestTime(new Date());
			clientRequest.setRequestType(RequestType.DISTANCE_TO_COAST);
			String queryString = bufferedRequest.getQueryString();
			if(StringUtils.isBlank(queryString)) {
				queryString = "";
			} else {
				queryString = "?" + queryString;
			}
			clientRequest.setRequestUrl(bufferedRequest.getRequestURL().toString() + queryString);
			clientRequest.setSourceIp(servletRequest.getRemoteAddr());
			
			LOG.info("Client request populated");
			try {
				String accept = httpServletRequest.getHeader("Accept");
				bufferedResponse.setContentType(MediaTypeHelper.getFirstValidType(accept));
				
				chain.doFilter(bufferedRequest, bufferedResponse);
				
				LOG.info("Request completed successfully");
				clientRequest.setResponseTime(new Date());
				// Log the response body without consuming the response stream
//				clientRequest.setResponseBody(bufferedResponse.getBody());
				clientRequest.setResponseBody(responseOutputStream.toString("utf-8"));
				clientRequest.setResponseStatus(bufferedResponse.getStatus());
				clientRequestService.save(clientRequest);
			} catch(KbsRestException e) {
				LOG.error("Request failed!", e);
				clientRequest.setError(Boolean.TRUE);
				clientRequest.setResponseTime(new Date());
				// Log the response body without consuming the response stream
				clientRequest.setResponseBody(bufferedResponse.getBody());
				clientRequest.setResponseStatus(bufferedResponse.getStatus());
				clientRequestService.save(clientRequest);
				LOG.info("Client request info saved before rethrowing exception");
				throw e;
			}
			
			return;
		} catch(IOException e) {
			LOG.error("Could not authorize request for client ID " + (null == clientId ? "null" : clientId), e);
			KbsRestException ex = new HttpInternalServerErrorException(e.getMessage(), e.getCause());
			respondWithError(bufferedRequest, bufferedResponse, ex);
		} catch(KbsRestException e) {
			LOG.error("Exception thrown while processing request", e);
			respondWithError(bufferedRequest, bufferedResponse, e);
		}
	}
	
	protected void respondWithError(HttpServletRequest request, HttpServletResponse response, KbsRestException e) throws IOException {
		response.setStatus(e.getHttpStatus().value());
		response.setContentType(MediaTypeHelper.getFirstValidType(request.getHeader("Accept"))); //MediaType.APPLICATION_JSON_VALUE);
		
		HttpErrorModel errorModel = new HttpErrorModel(e);
		RequestError requestError = fillRequestError(request, response, e.getClientId());
		requestError.setResponseBody(errorModel.toJson());
		requestError.setResponseTime(new Date());
		requestErrorService.save(requestError);
		
		ObjectMapper mapper = new ObjectMapper(); 
		mapper.writeValue(response.getOutputStream(), errorModel);
	}
	
	protected RequestError fillRequestError(HttpServletRequest request, HttpServletResponse response, Integer clientId) throws IOException {
		RequestError requestError = new RequestError();
		requestError.setClientId(clientId);
		requestError.setSourceIp(request.getRemoteAddr());
		
		String requestBody = IOUtils.toString(request.getInputStream(), "UTF-8");
		requestError.setRequestBody(requestBody);
		requestError.setRequestTime(new Date());
		
		// When there are multiple request types available, this should be based on the request type matching the requested endpoint
		String path = request.getPathInfo();
		if(path.contains("coastal-distance")) {
			requestError.setRequestType(RequestType.DISTANCE_TO_COAST);
		} else if(path.contains("fire-station")) {
			requestError.setRequestType(RequestType.FIRE_STATION);
		}
		
		requestError.setRequestUrl(request.getRequestURI());
		return requestError;
	}
	
}
