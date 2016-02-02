package com.kbs.geo.coastal.dao;

import java.util.List;

import com.kbs.geo.coastal.model.billing.ClientAuthWeb;

public interface ClientAuthWebDao {
	Integer save(ClientAuthWeb clientAuthWeb);
	ClientAuthWeb get(Integer id);
	ClientAuthWeb getByClientContactId(Integer clientContactId);
	List<ClientAuthWeb> getByClientId(Integer clientId);
	List<ClientAuthWeb> getByClientAuthId(Integer clientAuthId);
	ClientAuthWeb getByUsername(String username);
	ClientAuthWeb getByUsernamePassword(String username, String encodedPassword);
}
