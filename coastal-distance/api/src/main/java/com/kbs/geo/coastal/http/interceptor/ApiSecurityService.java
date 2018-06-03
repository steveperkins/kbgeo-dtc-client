package com.kbs.geo.coastal.http.interceptor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kbs.geo.coastal.http.exception.ExpiredSecurityTokenException;
import com.kbs.geo.coastal.http.exception.InvalidSecurityTokenException;
import com.kbs.geo.coastal.http.exception.IpNotAuthorizedException;
import com.kbs.geo.coastal.http.exception.KbsRestException;
import com.kbs.geo.coastal.http.exception.MissingSecurityTokenException;
import com.kbs.geo.coastal.http.exception.RefererNotAuthorizedException;
import com.kbs.geo.coastal.http.exception.RequestLimitReachedException;
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
	private static final Logger LOG = Logger.getLogger(ApiSecurityService.class);
	
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
	
	private Date lastAuthRefererLookup = new Date();
	private Map<Integer, List<ClientAuthReferer>> clientAuthIdToAuthReferersMap = new HashMap<Integer, List<ClientAuthReferer>>();
	
	private Date lastContractLookup = new Date();
	private Map<Integer, List<ClientContract>> clientIdToContractsMap = new HashMap<Integer, List<ClientContract>>();
	
	private Date lastAuthIpLookup = new Date();
	private Map<Integer, List<ClientAuthIp>> clientAuthIdToAuthIpsMap = new HashMap<Integer, List<ClientAuthIp>>();
	
	public ClientAuth authenticateRequest(String authToken, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, ServletException, InvalidSecurityTokenException {
		if(StringUtils.isBlank(authToken)) {
			// No token provided
			KbsRestException ex = new MissingSecurityTokenException();
			recordError(null, ex, httpServletRequest, httpServletResponse);
			throw ex;
		}
		LOG.info("Unverified auth token found");
		
		ClientAuth clientAuth = clientAuthService.getByToken(authToken);
		// Token provided but does not exist in our database
		if(null == clientAuth || null == clientAuth.getClientId()) {
			KbsRestException ex = new InvalidSecurityTokenException();
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
		
		Calendar fiveMinutesAgoCal = Calendar.getInstance();
		fiveMinutesAgoCal.roll(Calendar.MINUTE, -5);
		Date fiveMinutesAgo = fiveMinutesAgoCal.getTime();
		
		// Find the contract associated with this client
		List<ClientContract> clientContracts;
		if(lastContractLookup.after(fiveMinutesAgo) && clientIdToContractsMap.containsKey(clientAuth.getClientId())) {
			clientContracts = clientIdToContractsMap.get(clientAuth.getClientId());
		} else {
			clientContracts = clientContractService.getByClientId(clientAuth.getClientId());
			lastContractLookup = new Date();
			clientIdToContractsMap.put(clientAuth.getClientId(), clientContracts);
		}
		if(null == clientContracts || clientContracts.isEmpty()) {
			KbsRestException ex = new ServiceNotAllocatedException();
			recordError(null, ex, httpServletRequest, httpServletResponse);
			throw ex;
		}
		LOG.info("Client contracts found with this token");
		
		// Check for source IP filters
		List<ClientAuthIp> authIps;
		if(lastAuthIpLookup.after(fiveMinutesAgo) && clientAuthIdToAuthIpsMap.containsKey(clientAuth.getId())) {
			authIps = clientAuthIdToAuthIpsMap.get(clientAuth.getId());
		} else {
			authIps = clientAuthIpService.getByClientAuthId(clientAuth.getId());
			lastAuthIpLookup = new Date();
			clientAuthIdToAuthIpsMap.put(clientAuth.getId(), authIps);
		}
		String sourceIp = httpServletRequest.getRemoteAddr();
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
		LOG.info(String.format("Source IP (%s) is OK", sourceIp));
		
		// Check for referrer filters
		List<ClientAuthReferer> authReferers;
		if(lastAuthRefererLookup.after(fiveMinutesAgo) && clientAuthIdToAuthReferersMap.containsKey(clientAuth.getId())) {
			authReferers = clientAuthIdToAuthReferersMap.get(clientAuth.getId());
		} else {
			authReferers = clientAuthRefererService.getByClientAuthId(clientAuth.getId());
			lastAuthRefererLookup = new Date();
			clientAuthIdToAuthReferersMap.put(clientAuth.getId(), authReferers);
		}
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
			
			LOG.info(String.format("Referrer is %s", referrer));
			referrer = getNakedUrlDomain(referrer);
			LOG.info(String.format("Naked referrer is %s", referrer));
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
		String path = httpServletRequest.getPathInfo();
		RequestType requiredContract = null;
		if(path.contains("coastal-distance")) {
			requiredContract = RequestType.DISTANCE_TO_COAST;
		} else if(path.contains("fire-station")) {
			requiredContract = RequestType.FIRE_STATION;
		}
		List<ClientContract> contractsMatchingRequestType = findContractsMatching(clientContracts, requiredContract);
		if(null == contractsMatchingRequestType || contractsMatchingRequestType.isEmpty()) {
			KbsRestException ex = new ServiceNotAllocatedException();
			recordError(null, ex, httpServletRequest, httpServletResponse);
			throw ex;
		}
		LOG.info("Found client contracts matching " + requiredContract.name());
		
		ClientContract validContract = findValidContract(contractsMatchingRequestType);
		if(null == validContract) {
			KbsRestException ex = new ServiceNotAllocatedException();
			recordError(null, ex, httpServletRequest, httpServletResponse);
			throw ex;
		}
		LOG.info("Found valid contract for this auth token");
		
		// -1 == no limit
		if(validContract.getMaxRequests() > -1) {
			Calendar cal = Calendar.getInstance();
			Date today = cal.getTime();
			// First of the month
			cal.set(Calendar.DATE, 1);
			
			Long currentRequestCount = clientRequestService.getRequestCount(clientId, cal.getTime(), today, requiredContract);
			if(currentRequestCount >= validContract.getMaxRequests()) {
				KbsRestException ex = new RequestLimitReachedException();
				recordError(null, ex, httpServletRequest, httpServletResponse);
				LOG.error("Client " + clientId + " is over limit: " + currentRequestCount + " of " + validContract.getMaxRequests() + " successful requests have been received this month", ex);	
			}
		}
		return clientAuth;
	}
	
	private List<ClientContract> findContractsMatching(List<ClientContract> contracts, RequestType requestType) {
		// Roll through the list of the client's contracts and return those applicable to this request type
		List<ClientContract> matchingContracts = new ArrayList<ClientContract>();
		for(ClientContract contract: contracts) {
			if(contract.getRequestType().equals(requestType)) {
				matchingContracts.add(contract);
			}
		}
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
