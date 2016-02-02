package com.kbs.geo.coastal.service;

import java.util.List;

import com.kbs.geo.coastal.model.billing.ClientAuth;

public interface ClientAuthService {
	Integer save(ClientAuth clientAuth);
	ClientAuth get(Integer id);
	List<ClientAuth> getByClientId(Integer clientId);
	ClientAuth getByToken(String token);
}
