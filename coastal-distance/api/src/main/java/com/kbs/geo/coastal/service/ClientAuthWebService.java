package com.kbs.geo.coastal.service;

import java.util.List;

import com.kbs.geo.coastal.model.billing.ClientAuthWeb;

public interface ClientAuthWebService {
	Integer save(ClientAuthWeb ip);
	ClientAuthWeb get(Integer id);
	List<ClientAuthWeb> getByClientId(Integer clientId);
	List<ClientAuthWeb> getByClientAuthId(Integer clientAuthId);
	ClientAuthWeb getByClientContactId(Integer clientContactId);
	ClientAuthWeb getByUsername(String username);
	ClientAuthWeb getByUsernamePassword(String username, String encodedPassword);
}
