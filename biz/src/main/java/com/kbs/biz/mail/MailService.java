package com.kbs.biz.mail;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class MailService {
	private static final Logger LOG = Logger.getLogger(MailService.class);

	@Autowired
	@Qualifier("mailSmtpAuth")
	private String mailSmtpAuth;
	@Autowired
	@Qualifier("mailSmtpHost")
	private String mailSmtpHost;
	@Autowired
	@Qualifier("mailSmtpPort")
	private String mailSmtpPort;
	@Autowired
	@Qualifier("mailSmtpSocketFactoryClass")
	private String mailSmtpSocketFactoryClass;
	@Autowired
	@Qualifier("mailSmtpSocketFactoryFallback")
	private String mailSmtpSocketFactoryFallback;
	@Autowired
	@Qualifier("mailSmtpSocketFactoryPort")
	private String mailSmtpSocketFactoryPort;
	@Autowired
	@Qualifier("mailSslFactory")
	private String mailSslFactory;
	@Autowired
	@Qualifier("mailAddressBilling")
	private String mailAddressBilling;
	@Autowired
	@Qualifier("mailPasswordBilling")
	private String mailPasswordBilling;
	@Autowired
	@Qualifier("mailAddressNotification")
	private String mailAddressNotification;
	@Autowired
	@Qualifier("mailPasswordNotification")
	private String mailPasswordNotification;
	
	private final Map<String, String> passwords = new HashMap<String, String>();
	private final Properties properties = new Properties();
	
	@PostConstruct
	public void setProperties() {
		properties.setProperty("mail.smtp.host", mailSmtpHost);
//        properties.setProperty("mail.smtp.socketFactory.class", mailSmtpSocketFactoryClass);
//        properties.setProperty("mail.smtp.socketFactory.fallback", mailSmtpSocketFactoryFallback);
        properties.setProperty("mail.smtp.port", mailSmtpPort);
//        properties.setProperty("mail.smtp.socketFactory.port", mailSmtpSocketFactoryPort);
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.auth", mailSmtpAuth);
        /*properties.put("mail.debug", "true");
        properties.put("mail.debug.auth", "true");
        properties.put("mail.store.protocol", "pop3");
        properties.put("mail.transport.protocol", "smtp");
        properties.setProperty( "mail.pop3.socketFactory.fallback", "false");*/
        
        passwords.put(mailAddressNotification, mailPasswordNotification);
        passwords.put(mailAddressBilling, mailPasswordBilling);
	}
	
	public void sendMail(final MailMessage message) {
        Session session = Session.getDefaultInstance(properties, new javax.mail.Authenticator() {   
        	@Override
            protected PasswordAuthentication getPasswordAuthentication() {   
        		return new PasswordAuthentication(message.getFromAddress(), passwords.get(message.getFromAddress()));
            }
        });
        
        try {  
            Transport.send(message.toMimeMessage(session));
            LOG.info("Email sent");
        } catch (MessagingException e) {
        	LOG.error("Could not send email", e);
        }
	}
}
