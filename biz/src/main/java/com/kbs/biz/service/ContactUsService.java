package com.kbs.biz.service;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.kbs.biz.db.ContactUsDao;
import com.kbs.biz.db.FreeTrialDao;
import com.kbs.biz.mail.MailService;
import com.kbs.biz.mail.sparkpost.SparkMailService;
import com.kbs.biz.model.ContactUs;

@Service
public class ContactUsService {
	
	@Autowired
	ContactUsDao dao;
	
	@Autowired
	MailService mailService;
	
	@Autowired
	SparkMailService clientMailService;
	
	@Autowired
	FreeTrialDao freeTrialDao;
	
//	@Resource(name="trialkey.url")
//	String trialKeyUrl;
//	
//	@Resource(name="trialkey.authtoken")
//	String trialKeyAuthToken;
	
	public ContactUs save(ContactUs contactUs) {
		contactUs = dao.save(contactUs);
		if(null != contactUs.getId()) {
//			mailService.sendMail(new ContactUsMailMessage(contactUs));
			clientMailService.sendContactUsMessage(contactUs);
		}
		
		// If the guest requested a free trial key, generate one and send via email
		if(contactUs.getRequestFreeTrial()) {
			try {
				String apiKey = freeTrialDao.createFreeTrial(contactUs);
//				ResponseEntity<String> response = restTemplate.exchange(trialKeyUrl, 
//		    			HttpMethod.POST,
//		    	        new HttpEntity<>(contactUs, createHeaders()), 
//		    	        String.class);
//				
//				if(response.getStatusCode().equals(HttpStatus.OK)) {
//					String apiKey = response.getBody();
					clientMailService.sendFreeTrialApiKeyMessage(contactUs, apiKey);
//				}
//				String apiKey = restTemplate.getForObject(trialKeyUrl, String.class);
			} catch(Exception e) {
				System.err.println("Something went wrong when attempting to generate a new trial key!!!");
				e.printStackTrace();
			}
		}
		
		return contactUs;
	}
//	
//	private HttpHeaders createHeaders() {
//    	
//		   return new HttpHeaders() {
//		      {
//		         set("kb-auth-token", trialKeyAuthToken);
//		         set("Content-Type", "application/json");
//		      }
//		   };
//	 }
}
