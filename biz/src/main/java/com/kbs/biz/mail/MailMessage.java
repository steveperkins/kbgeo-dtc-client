package com.kbs.biz.mail;

import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.StringUtils;

public class MailMessage {
	protected static String LINE_BREAK = "\r\n";
	
	private String fromAddress;
	private List<String> toAddresses;
	private List<String> ccAddresses;
	private List<String> bccAddresses;
	private String subject;
	private String body;
	
	public String getFromAddress() {
		return fromAddress;
	}
	public MailMessage withFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
		return this;
	}
	public List<String> getToAddresses() {
		return toAddresses;
	}
	public MailMessage withToAddresses(List<String> toAddresses) {
		this.toAddresses = toAddresses;
		return this;
	}
	public MailMessage withToAddress(String toAddress) {
		if(null == this.toAddresses) this.toAddresses = new ArrayList<String>();
		this.toAddresses.add(toAddress);
		return this;
	}
	public List<String> getCcAddresses() {
		return ccAddresses;
	}
	public MailMessage withCcAddresses(List<String> ccAddresses) {
		this.ccAddresses = ccAddresses;
		return this;
	}
	public MailMessage withCcAddress(String ccAddress) {
		if(null == this.ccAddresses) this.ccAddresses = new ArrayList<String>();
		this.ccAddresses.add(ccAddress);
		return this;
	}
	public List<String> getBccAddresses() {
		return bccAddresses;
	}
	public MailMessage withBccAddresses(List<String> bccAddresses) {
		this.bccAddresses = bccAddresses;
		return this;
	}
	public MailMessage withBccAddress(String bccAddress) {
		if(null == this.bccAddresses) this.bccAddresses = new ArrayList<String>();
		this.bccAddresses.add(bccAddress);
		return this;
	}
	public String getSubject() {
		return subject;
	}
	public MailMessage withSubject(String subject) {
		this.subject = subject;
		return this;
	}
	public String getBody() {
		return body;
	}
	public MailMessage withBody(String body) {
		this.body = body;
		return this;
	}
	
	public MimeMessage toMimeMessage(Session session) throws AddressException, MessagingException {
		MimeMessage mimeMessage = new MimeMessage(session);
		mimeMessage.setFrom(new InternetAddress(getFromAddress()));
		mimeMessage.setSender(mimeMessage.getFrom()[0]);
		if(null != toAddresses && !toAddresses.isEmpty()) mimeMessage.setRecipients(MimeMessage.RecipientType.TO, InternetAddress.parse(StringUtils.join(toAddresses, ",")));
		if(null != ccAddresses && !ccAddresses.isEmpty()) mimeMessage.setRecipients(MimeMessage.RecipientType.CC, InternetAddress.parse(StringUtils.join(ccAddresses, ",")));
		if(null != bccAddresses && !bccAddresses.isEmpty()) mimeMessage.setRecipients(MimeMessage.RecipientType.BCC, InternetAddress.parse(StringUtils.join(bccAddresses, ",")));
        mimeMessage.setSubject(getSubject());
        mimeMessage.setText(getBody());
        return mimeMessage;
	}
	
}
