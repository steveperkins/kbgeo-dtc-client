package com.kbs.geo.coastal.model.billing;

import java.util.Date;

import com.kbs.geo.coastal.model.Identifiable;

public class ClientRequest implements Identifiable {
	private Integer id;
	private ClientAuth clientAuth;
	private Integer clientAuthId;
	private String sourceIp;
	private String requestUrl;
	private String requestBody;
	private RequestType requestType;
	private Integer responseStatus;
	private String responseBody;
	private Date requestTime;
	private Date responseTime;
	private Boolean error;
	private String errorMessage;
	private Date created;
	private Date updated;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public ClientAuth getClientAuth() {
		return clientAuth;
	}
	public void setClientAuth(ClientAuth clientAuth) {
		this.clientAuth = clientAuth;
	}
	public Integer getClientAuthId() {
		return clientAuthId;
	}
	public void setClientAuthId(Integer clientAuthId) {
		this.clientAuthId = clientAuthId;
	}
	public String getSourceIp() {
		return sourceIp;
	}
	public void setSourceIp(String sourceIp) {
		this.sourceIp = sourceIp;
	}
	public String getRequestUrl() {
		return requestUrl;
	}
	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}
	public String getRequestBody() {
		return requestBody;
	}
	public void setRequestBody(String requestBody) {
		this.requestBody = requestBody;
	}
	public RequestType getRequestType() {
		return requestType;
	}
	public void setRequestType(RequestType requestType) {
		this.requestType = requestType;
	}
	public Integer getResponseStatus() {
		return responseStatus;
	}
	public void setResponseStatus(Integer responseStatus) {
		this.responseStatus = responseStatus;
	}
	public String getResponseBody() {
		return responseBody;
	}
	public void setResponseBody(String responseBody) {
		this.responseBody = responseBody;
	}
	public Date getRequestTime() {
		return requestTime;
	}
	public void setRequestTime(Date requestTime) {
		this.requestTime = requestTime;
	}
	public Date getResponseTime() {
		return responseTime;
	}
	public void setResponseTime(Date responseTime) {
		this.responseTime = responseTime;
	}
	public Boolean getError() {
		return error;
	}
	public void setError(Boolean error) {
		this.error = error;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
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
