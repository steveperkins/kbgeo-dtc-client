package com.kbs.geo.coastal.service;

import java.util.List;

import com.kbs.geo.coastal.model.billing.ClientContract;

public interface ClientContractService {
	Integer save(ClientContract clientAuth);
	ClientContract get(Integer id);
	List<ClientContract> getByClientId(Integer clientId);
}
