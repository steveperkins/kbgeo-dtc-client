package com.kbs.mail;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MailService {
	private static final Logger LOG = LoggerFactory.getLogger(MailService.class);
	private final String zohoAuthToken = "f1c614fea28fd63db46a549c8b4d92cb";
	private String mailSmtpAuth;
	private String mailSmtpHost;
	private String mailSmtpPort;
/*	private String mailSmtpSocketFactoryClass;
	private String mailSmtpSocketFactoryFallback;
	private String mailSmtpSocketFactoryPort;
	private String mailSslFactory;
	private String mailAddressBilling;
	private String mailPasswordBilling;
	private String mailAddressNotification;
	private String mailPasswordNotification;*/
	
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
	}
	public void sendMail(final MailMessage message) {
        Session session = Session.getDefaultInstance(properties, new javax.mail.Authenticator() {   
        	@Override
            protected PasswordAuthentication getPasswordAuthentication() {   
        		return new PasswordAuthentication("billing@kbgeo.com", "kbBILL2525");
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
