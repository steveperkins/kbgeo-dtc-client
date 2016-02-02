package com.kbs.geo.coastal.service;

import java.util.List;

import com.kbs.geo.coastal.model.billing.ClientAuthReferer;

public interface ClientAuthRefererService {
	Integer save(ClientAuthReferer clientAuth);
	ClientAuthReferer get(Integer id);
	List<ClientAuthReferer> getByClientId(Integer clientId);
	List<ClientAuthReferer> getByClientAuthId(Integer clientAuthId);
}
