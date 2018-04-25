package com.kbs.biz.mail.sparkpost;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.kbs.biz.model.ContactUs;

@Service
public class SparkMailService {

    private Log logger = LogFactory.getLog(SparkMailService.class);

    @Autowired
    private RestTemplate restTemplate;
    
    @Resource(name="mailSparkpostKey")
    private String mailSparkpostKey;
    
    @Resource(name="mailSparkpostUrl")
    private String mailSparkpostUrl;
    
    @Resource(name="mailSparkpostFrom")
    private String mailSparkpostFrom;

    public void sendEmail(SparkMailMessage message) throws EmailException {
    	try {
    		String json = message.toJson();
    		logger.debug("Sending email: " + json);
    		ResponseEntity<String> response = restTemplate.exchange(mailSparkpostUrl + "/transmissions", 
    			HttpMethod.POST,
    	        new HttpEntity<>(message.toJson(), createHeaders()), 
    	        String.class);
    	logger.info("Email sent");
    	} catch(HttpClientErrorException e) {
    		logger.error("Could not send email: " + e.getResponseBodyAsString(), e);
    	}
    }
    
	public Boolean sendFreeTrialApiKeyMessage(ContactUs contactUs, String apiKey, String contractType) {
		try {
			sendEmail(
					new SparkMailMessage()
					.withContent(new FreeTrialApiKeyMessage(contactUs.getName(), apiKey, contactUs.getContractType()))
					.withRecipients(contactUs.getEmail()));
			return Boolean.TRUE;
		} catch (EmailException e) {
			logger.error(String.format("Could not send free trial key message to %s", contactUs.getEmail()));
			return Boolean.FALSE;
		}
	}
	
	public Boolean sendContactUsMessage(ContactUs contactUs) {
		try {
			sendEmail(
					new SparkMailMessage()
					.withContent(new ContactUsMessage(contactUs))
					.withRecipients("perkins.steve@gmail.com", "sales@kbgeo.com"));
			return Boolean.TRUE;
		} catch (EmailException e) {
			logger.error(String.format("Could not send new contact us message from %s", contactUs.getEmail()));
			return Boolean.FALSE;
		}
	}
	
	private HttpHeaders createHeaders() {
	    	
		   return new HttpHeaders() {
		      {
		         set("Authorization", mailSparkpostKey);
		         set("Content-Type", "application/json");
		      }
		   };
	 }

}
