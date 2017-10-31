package com.kbs.geo.coastal.http;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.kbs.biz.model.ContactUs;
import com.kbs.geo.coastal.http.exception.IpNotAuthorizedException;
import com.kbs.geo.coastal.model.billing.Client;
import com.kbs.geo.coastal.model.billing.ClientAuth;
import com.kbs.geo.coastal.model.billing.ClientContact;
import com.kbs.geo.coastal.model.billing.ClientContract;
import com.kbs.geo.coastal.model.billing.RequestType;
import com.kbs.geo.coastal.service.ClientAuthService;
import com.kbs.geo.coastal.service.ClientContactService;
import com.kbs.geo.coastal.service.ClientContractService;
import com.kbs.geo.coastal.service.ClientService;

@RestController
public class ApiController {
	
	@Resource(name="application.version")
	private String version;
	
	@Autowired
	private ClientService clientService;
	
	@Autowired
	private ClientContactService contactService;
	
	@Autowired
	private ClientContractService contractService;
	
	@Autowired
	private ClientAuthService authService;
	
	private String localIpAddress;
	public ApiController() {
		try {
			InetAddress ipAddr = InetAddress.getLocalHost();
			localIpAddress = ipAddr.getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}	
	}
	
	@RequestMapping(value="/version", method=RequestMethod.GET)
    public @ResponseBody String getVersion() {
		return version;
    }
	
	@RequestMapping(value="/trialkey", method=RequestMethod.POST)
	public @ResponseBody String createTrialKey(HttpServletRequest request, ContactUs contactUs) {
		if(StringUtils.isBlank(localIpAddress)) {
			throw new IpNotAuthorizedException();
		}
//		if(!localIpAddress.equals(getClientIpAddress(request))) {
//			throw new IpNotAuthorizedException("Unauthorized trial key attempt");
//		}
		
		String apiKey = UUID.randomUUID().toString();
		Client client = new Client();
		client.setName(contactUs.getName());
		client.setAddress("No address specified");
		client.setCity("No city");
		client.setState("WI");
		client.setPhone(contactUs.getPhone());
		client.setZip("53716");
		client.setId(clientService.save(client));
		
		ClientContact contact = new ClientContact();
		contact.setAddress(client.getAddress());
		contact.setCity(client.getCity());
		contact.setState(client.getState());
		contact.setFirstName(contactUs.getName());
		contact.setLastName("");
		contact.setEmail(contactUs.getEmail());
		contact.setClientId(client.getId());
		contact.setClient(client);
		contact.setId(contactService.save(contact));
		
		ClientContract contract = new ClientContract();
		contract.setCentsPerRequest(0);
		contract.setClientId(client.getId());
		contract.setExpires(new DateTime().plusDays(14).toDate());
		contract.setMaxRequests(100L);
		contract.setName(contactUs.getCompany());
		contract.setRequestType(RequestType.DISTANCE_TO_COAST);
		contract.setStarts(new Date());
		contract.setClient(client);
		contract.setId(contractService.save(contract));
		
		ClientAuth auth = new ClientAuth();
		auth.setClientId(client.getId());
		auth.setExpires(contract.getExpires());
		auth.setName(contactUs.getCompany());
		auth.setToken(apiKey);
		auth.setCreated(contract.getStarts());
		auth.setClient(client);
		auth.setId(authService.save(auth));
		
		return apiKey;
	}
	
	
	private String getClientIpAddress(HttpServletRequest request) {
	    String xForwardedForHeader = request.getHeader("X-Forwarded-For");
	    if (xForwardedForHeader == null) {
	        return request.getRemoteAddr();
	    } else {
	        // As of https://en.wikipedia.org/wiki/X-Forwarded-For
	        // The general format of the field is: X-Forwarded-For: client, proxy1, proxy2 ...
	        // we only want the client
	        return new StringTokenizer(xForwardedForHeader, ",").nextToken().trim();
	    }
	}
}
