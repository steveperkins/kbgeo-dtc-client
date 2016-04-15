package com.kbs.geo.coastal.http.interceptor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kbs.geo.coastal.http.exception.ExpiredSecurityTokenException;
import com.kbs.geo.coastal.http.exception.InvalidSecurityTokenException;
import com.kbs.geo.coastal.http.exception.IpNotAuthorizedException;
import com.kbs.geo.coastal.http.exception.KbsRestException;
import com.kbs.geo.coastal.http.exception.MissingSecurityTokenException;
import com.kbs.geo.coastal.http.exception.RefererNotAuthorizedException;
import com.kbs.geo.coastal.http.exception.ServiceNotAllocatedException;
import com.kbs.geo.coastal.model.billing.ClientAuth;
import com.kbs.geo.coastal.model.billing.ClientAuthIp;
import com.kbs.geo.coastal.model.billing.ClientAuthReferer;
import com.kbs.geo.coastal.model.billing.ClientContract;
import com.kbs.geo.coastal.model.billing.RequestError;
import com.kbs.geo.coastal.model.billing.RequestType;
import com.kbs.geo.coastal.service.ClientAuthIpService;
import com.kbs.geo.coastal.service.ClientAuthRefererService;
import com.kbs.geo.coastal.service.ClientAuthService;
import com.kbs.geo.coastal.service.ClientContractService;
import com.kbs.geo.coastal.service.ClientRequestService;
import com.kbs.geo.coastal.service.RequestErrorService;

@Component
public class ApiSecurityService {
	private static final Logger LOG = LoggerFactory.getLogger(ApiSecurityService.class);
	
	@Autowired
	private ClientRequestService clientRequestService;
	
	@Autowired
	private ClientAuthService clientAuthService;
	
	@Autowired
	private ClientAuthIpService clientAuthIpService;
	
	@Autowired
	private ClientAuthRefererService clientAuthRefererService;
	
	@Autowired
	private ClientContractService clientContractService;
	
	@Autowired
	private RequestErrorService requestErrorService;
	
	public ClientAuth authenticateRequest(String authToken, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, ServletException, InvalidSecurityTokenException {
		if(StringUtils.isBlank(authToken)) {
			// No token provided
			KbsRestException ex = new InvalidSecurityTokenException();
			recordError(null, ex, httpServletRequest, httpServletResponse);
			throw ex;
		}
		LOG.info("Unverified auth token found");
		
		ClientAuth clientAuth = clientAuthService.getByToken(authToken);
		// Token provided but does not exist in our database
		if(null == clientAuth || null == clientAuth.getClientId()) {
			KbsRestException ex = new MissingSecurityTokenException();
			recordError(null, ex, httpServletRequest, httpServletResponse);
			throw ex;
		}
		LOG.info("Auth token verified to exist");
		
		Integer clientId = clientAuth.getClientId();
		
		// Token provided but has expired
		if(null != clientAuth.getExpires() && new Date().after(clientAuth.getExpires())) {
			KbsRestException ex = new ExpiredSecurityTokenException();
			recordError(null, ex, httpServletRequest, httpServletResponse);
			throw ex;
		}
		LOG.info("Auth token verified to be unexpired");
		
		// Find the contract associated with this client
		List<ClientContract> clientContracts = clientContractService.getByClientId(clientAuth.getClientId());
		if(null == clientContracts || clientContracts.isEmpty()) {
			KbsRestException ex = new ServiceNotAllocatedException();
			recordError(null, ex, httpServletRequest, httpServletResponse);
			throw ex;
		}
		LOG.info("Client contracts found with this token");
		
		// Check for source IP filters
		String sourceIp = httpServletRequest.getRemoteAddr();
		List<ClientAuthIp> authIps = clientAuthIpService.getByClientAuthId(clientAuth.getId());
		if(null != authIps && !authIps.isEmpty()) {
			// If the originating IP doesn't match any of the authorized IPs, prevent access
			Boolean ipFound = Boolean.FALSE;
			for(ClientAuthIp ip: authIps) {
				if(sourceIp.equals(ip.getIp())) {
					ipFound = Boolean.TRUE;
					break;
				}
			}
			
			if(!ipFound) {
				KbsRestException ex = new IpNotAuthorizedException(String.format("Source IP '%s' is not allowed", sourceIp));
				recordError(null, ex, httpServletRequest, httpServletResponse);
				throw ex;
			}
		}
		LOG.info("Source IP ({}) is OK", sourceIp);
		
		// Check for source IP filters
		List<ClientAuthReferer> authReferers = clientAuthRefererService.getByClientAuthId(clientAuth.getId());
		if(null != authReferers && !authReferers.isEmpty()) {
			// If the originating IP doesn't match any of the authorized IPs, prevent access
			String referrer = httpServletRequest.getHeader("referer");
			// Firefox no longer sends the referer header (WTF), so if necessary try to use the Origin header
			if(StringUtils.isBlank(referrer)) referrer = httpServletRequest.getHeader("Origin");
			if(StringUtils.isBlank(referrer)) {
				// If any referer filters have been defined, not sending a referer results in an unauthorized request
				KbsRestException ex = new RefererNotAuthorizedException("Empty referer is not allowed when referer filters have been defined");
				recordError(null, ex, httpServletRequest, httpServletResponse);
				throw ex;
			}
			
			LOG.info("Referrer is {}", referrer);
			referrer = getNakedUrlDomain(referrer);
			LOG.info("Naked referrer is {}", referrer);
			Boolean referrerFound = Boolean.FALSE;
			for(ClientAuthReferer authReferer: authReferers) {
				List<String> separatedReferers = Arrays.asList(authReferer.getReferers().split(","));
				
				if(separatedReferers.contains(referrer)) {
					referrerFound = Boolean.TRUE;
					break;
				}
			}
			
			if(!referrerFound) {
				KbsRestException ex = new RefererNotAuthorizedException(String.format("Referer '%s' is not allowed", referrer));
				recordError(null, ex, httpServletRequest, httpServletResponse);
				throw ex;
			}
		}
					
		LOG.info("Referer is OK");
		
		List<ClientContract> contractsMatchingRequestType = findContractsMatching(clientContracts, RequestType.DISTANCE_TO_COAST);
		if(null == contractsMatchingRequestType || contractsMatchingRequestType.isEmpty()) {
			KbsRestException ex = new ServiceNotAllocatedException();
			recordError(null, ex, httpServletRequest, httpServletResponse);
			throw ex;
		}
		LOG.info("Found client contracts matching the DISTANCE_TO_COAST");
		
		ClientContract validContract = findValidContract(contractsMatchingRequestType);
		if(null == validContract) {
			KbsRestException ex = new ServiceNotAllocatedException();
			recordError(null, ex, httpServletRequest, httpServletResponse);
			throw ex;
		}
		LOG.info("Found valid contract for this auth token");
		
		return clientAuth;
	}
	
	private List<ClientContract> findContractsMatching(List<ClientContract> contracts, RequestType requestType) {
		// TODO When there are multiple possible request types, roll through the list of the client's contracts and return those applicable to this request type
		List<ClientContract> matchingContracts = new ArrayList<ClientContract>();
		matchingContracts.add(contracts.get(0));
		return matchingContracts;
	}
	
	/**
	 * Checks contract dates and returns the first valid contract
	 * @param contracts
	 * @return
	 */
	private ClientContract findValidContract(List<ClientContract> contracts) {
		Date today = new Date();
		for(ClientContract contract: contracts) {
			if(null == contract.getExpires()) return contract;
			if(today.before(contract.getStarts())) continue;
			if(today.after(contract.getExpires())) continue;
			// This contract is currently active
			return contract;
		}
		return null;
	}
	
	public void recordError(Integer clientId, KbsRestException exception, HttpServletRequest request, HttpServletResponse response) throws IOException {
		if(null == clientId) {
			
		} else {
			RequestError requestError = fillRequestError(request, response, clientId);
			requestError.setResponseTime(new Date());
			requestErrorService.save(requestError);
		}
	}
	
	private RequestError fillRequestError(HttpServletRequest request, HttpServletResponse response, Integer clientId) throws IOException {
		RequestError requestError = new RequestError();
		requestError.setClientId(clientId);
		requestError.setSourceIp(request.getRemoteAddr());
		
		String requestBody = IOUtils.toString(request.getInputStream(), "UTF-8");
		requestError.setRequestBody(requestBody);
		requestError.setRequestTime(new Date());
		
		// TODO When there are multiple request types available, this should be based on the request type matching the requested endpoint
		requestError.setRequestType(RequestType.DISTANCE_TO_COAST);
		requestError.setRequestUrl(request.getRequestURI());
		return requestError;
	}
	
	
	public String getNakedUrlDomain(String url) {
		if(null == url || StringUtils.isBlank(url)) return url;
		// First strip off the protocol if there is one
		int index = url.indexOf("://");
		if(index >= 0) {
			url = url.substring(index + 3);
		}
		
		// Now look for a port number
		index = url.indexOf(":");
		if(index >= 0) {
			url = url.substring(0, index);
		}
		
		// Now look for any context paths
		index = url.indexOf("/");
		if(index >= 0) {
			url = url.substring(0, index);
		}
		return url;
	}
}
