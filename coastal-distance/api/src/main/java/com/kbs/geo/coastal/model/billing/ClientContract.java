package com.kbs.geo.coastal.model.billing;

import java.util.Date;

import com.kbs.geo.coastal.model.Identifiable;

public class ClientContract implements Identifiable {
	private Integer id;
	// Foreign key to Client
	private Client client;
	private Integer clientId;
	private String name;
	private RequestType requestType;
	private Long maxRequests;
	private Integer centsPerRequest;
	// Contract start date
	private Date starts;
	// Contract expiration. Supercedes all other expiration dates.
	private Date expires;
	private Date created;
	private Date updated;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Client getClient() {
		return client;
	}
	public void setClient(Client client) {
		this.client = client;
	}
	public Integer getClientId() {
		return clientId;
	}
	public void setClientId(Integer clientId) {
		this.clientId = clientId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public RequestType getRequestType() {
		return requestType;
	}
	public void setRequestType(RequestType requestType) {
		this.requestType = requestType;
	}
	public Long getMaxRequests() {
		return maxRequests;
	}
	public void setMaxRequests(Long maxRequests) {
		this.maxRequests = maxRequests;
	}
	public Integer getCentsPerRequest() {
		return centsPerRequest;
	}
	public void setCentsPerRequest(Integer centsPerRequest) {
		this.centsPerRequest = centsPerRequest;
	}
	public Date getStarts() {
		return starts;
	}
	public void setStarts(Date starts) {
		this.starts = starts;
	}
	public Date getExpires() {
		return expires;
	}
	public void setExpires(Date expires) {
		this.expires = expires;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public Date getUpdated() {
		return updated;
	}
	public void setUpdated(Date updated) {
		this.updated = updated;
	}
	
}
