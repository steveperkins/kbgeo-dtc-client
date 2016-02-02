package com.kbs.geo.coastal.dao;

import java.util.List;

import com.kbs.geo.coastal.model.billing.ClientAuthIp;

public interface ClientAuthIpDao {
	Integer save(ClientAuthIp ip);
	ClientAuthIp get(Integer id);
	List<ClientAuthIp> getByClientId(Integer clientId);
	List<ClientAuthIp> getByClientAuthId(Integer clientAuthId);
}
