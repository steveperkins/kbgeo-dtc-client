package com.kbs.geo.coastal.model.billing;

import java.util.Date;

import org.springframework.stereotype.Component;

/**
 * Represents a client's authorized web user account.
 * @author Steve
 *
 */
@Component
public class ClientAuthWeb implements IClientAuthWeb {
	private Integer id;
	private ClientAuth clientAuth;
	private Integer clientAuthId;
	private ClientContact clientContact;
	private Integer clientContactId;
	private String username;
	private String password;
	private Date starts;
	private Date expires;
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
	public ClientContact getClientContact() {
		return clientContact;
	}
	public void setClientContact(ClientContact clientContact) {
		this.clientContact = clientContact;
	}
	public Integer getClientContactId() {
		return clientContactId;
	}
	public void setClientContactId(Integer clientContactId) {
		this.clientContactId = clientContactId;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
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
