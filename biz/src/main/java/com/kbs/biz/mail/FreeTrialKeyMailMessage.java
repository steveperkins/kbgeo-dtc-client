package com.kbs.biz.mail;

import com.kbs.biz.model.ContactUs;

public class FreeTrialKeyMailMessage extends MailMessage {
	private static String template = "Hi %s," + LINE_BREAK + LINE_BREAK + "Thank you for contacting KB Geo for fast, accurate, low-cost distance to coast! Here's the free trial API key you requested:" + LINE_BREAK + "%s" + LINE_BREAK + LINE_BREAK + "Ready to get started? See our developer documentation at http://docs.kbgeo.com. " + LINE_BREAK + LINE_BREAK + "If you have any questions, email us at sales@kbgeo.com and we'll be happy to help!" + LINE_BREAK + LINE_BREAK + "Thank you," + LINE_BREAK + LINE_BREAK + "Steve Perkins" + LINE_BREAK + "KB Geo" + LINE_BREAK + "http://www.kbgeo.com";
	private String apiKey;
	public FreeTrialKeyMailMessage(ContactUs contactUs, String apiKey) {
		this.apiKey = apiKey;
		withFromAddress("sales@kbgeo.com")
		.withToAddress(contactUs.getEmail())
		.withSubject("Your free KB Geo Distance to Coast API key")
		.withBody(getBody(contactUs));
	}
	
	private String getBody(ContactUs contactUs) {
		return String.format(template, contactUs.getName(), apiKey);
	}
}
