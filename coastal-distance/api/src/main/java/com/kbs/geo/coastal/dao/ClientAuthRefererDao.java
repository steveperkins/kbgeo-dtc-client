package com.kbs.geo.coastal.dao;

import java.util.List;

import com.kbs.geo.coastal.model.billing.ClientAuthReferer;

public interface ClientAuthRefererDao {
	Integer save(ClientAuthReferer clientAuth);
	ClientAuthReferer get(Integer id);
	List<ClientAuthReferer> getByClientId(Integer clientId);
	List<ClientAuthReferer> getByClientAuthId(Integer clientAuthId);
}
