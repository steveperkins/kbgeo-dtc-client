package com.kbs.geo.coastal.model.billing;

import java.util.Date;

import com.kbs.geo.coastal.model.Identifiable;

/**
 * Represents a client's authorized HTTP referer header value. Referers can only be tied to a ClientAuth token, and are only intended as an additional measure of security.
 * @author Steve
 *
 */
public class ClientAuthReferer implements Identifiable {
	private Integer id;
	private ClientAuth clientAuth;
	private Integer clientAuthId;
	private String name;
	private String referers;
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getReferers() {
		return referers;
	}
	public void setReferers(String referers) {
		this.referers = referers;
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
