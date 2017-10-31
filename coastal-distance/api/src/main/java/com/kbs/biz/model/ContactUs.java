package com.kbs.biz.model;

public class ContactUs {
	private Long id;
	String name;
	String company;
	String email;
	String phone;
	String message;
	Boolean requestFreeTrial = Boolean.FALSE;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Boolean getRequestFreeTrial() {
		return requestFreeTrial;
	}
	public void setRequestFreeTrial(Boolean requestFreeTrial) {
		if(null == requestFreeTrial) {
			requestFreeTrial = Boolean.FALSE;
		}
		this.requestFreeTrial = requestFreeTrial;
	}
}
