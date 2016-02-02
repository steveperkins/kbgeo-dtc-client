package com.kbs.geo.coastal.service;

import java.util.List;

import com.kbs.geo.coastal.model.billing.ClientAuthIp;

public interface ClientAuthIpService {
	Integer save(ClientAuthIp ip);
	ClientAuthIp get(Integer id);
	List<ClientAuthIp> getByClientId(Integer clientId);
	List<ClientAuthIp> getByClientAuthId(Integer clientAuthId);
}
