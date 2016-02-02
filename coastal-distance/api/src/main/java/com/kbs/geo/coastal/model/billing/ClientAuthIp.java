package com.kbs.geo.coastal.model.billing;

import java.util.Date;

import com.kbs.geo.coastal.model.Identifiable;

/**
 * Represents a client's authorized IP. IP addresses can only be tied to a ClientAuth token, and are only intended as an additional measure of security.
 * @author Steve
 *
 */
public class ClientAuthIp implements Identifiable {
	private Integer id;
	private ClientAuth clientAuth;
	private Integer clientAuthId;
	private String name;
	private String ip;
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
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
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
