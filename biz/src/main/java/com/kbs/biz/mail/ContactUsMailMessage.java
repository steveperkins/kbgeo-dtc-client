package com.kbs.biz.mail;

import com.kbs.biz.model.ContactUs;

public class ContactUsMailMessage extends MailMessage {
	
	public ContactUsMailMessage(ContactUs contactUs) {
		withFromAddress("mucketymuckety@gmail.com")
		.withToAddress("perkins.steve@gmail.com")
		.withSubject("New KB Geo contact request")
		.withBody(getBody(contactUs));
	}
	
	private String getBody(ContactUs contactUs) {
		return new StringBuilder()
			.append("A new KB Geo contact request has been received:").append(LINE_BREAK).append(LINE_BREAK)
			.append("Name: ").append(contactUs.getName()).append(LINE_BREAK)
			.append("Company: ").append(contactUs.getCompany()).append(LINE_BREAK)
			.append("Email: ").append(contactUs.getEmail()).append(LINE_BREAK)
			.append("Phone: ").append(contactUs.getPhone()).append(LINE_BREAK)
			.append("Message:").append(contactUs.getMessage()).append(LINE_BREAK)
			.toString();
	}
}
