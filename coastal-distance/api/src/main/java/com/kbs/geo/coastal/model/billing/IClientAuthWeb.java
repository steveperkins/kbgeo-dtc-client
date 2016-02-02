package com.kbs.geo.coastal.model.billing;

import java.util.Date;

import com.kbs.geo.coastal.model.Identifiable;

/**
 * Required so ClientAuthWeb can be request-scoped
 * @author Steve
 *
 */
public interface IClientAuthWeb extends Identifiable {
	public Integer getId();
	public void setId(Integer id);
	public ClientAuth getClientAuth();
	public void setClientAuth(ClientAuth clientAuth);
	public Integer getClientAuthId();
	public void setClientAuthId(Integer clientAuthId);
	public ClientContact getClientContact();
	public void setClientContact(ClientContact clientContact);
	public Integer getClientContactId();
	public void setClientContactId(Integer clientContactId);
	public String getUsername();
	public void setUsername(String username);
	public String getPassword();
	public void setPassword(String password);
	public Date getStarts();
	public void setStarts(Date starts);
	public Date getExpires();
	public void setExpires(Date expires);
	public Date getCreated();
	public void setCreated(Date created);
	public Date getUpdated();
	public void setUpdated(Date updated);
}
