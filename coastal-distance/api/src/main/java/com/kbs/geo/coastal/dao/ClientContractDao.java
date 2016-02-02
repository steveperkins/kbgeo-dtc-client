package com.kbs.geo.coastal.dao;

import java.util.List;

import com.kbs.geo.coastal.model.billing.ClientContract;

public interface ClientContractDao {
	Integer save(ClientContract clientAuth);
	ClientContract get(Integer id);
	List<ClientContract> getByClientId(Integer clientId);
}
