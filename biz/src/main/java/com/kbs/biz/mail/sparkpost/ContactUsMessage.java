package com.kbs.biz.mail.sparkpost;

import com.kbs.biz.model.ContactUs;

public class ContactUsMessage extends SparkMessageContent {

	public ContactUsMessage(ContactUs contactUs) {
		super();
		
		super.withTemplate("new-kbgeo-contact-request");
		super.addSubstitutionData("contactUs", contactUs);
	}
	
}
