package com.kbs.geo.coastal.service;

import java.util.List;

import com.kbs.geo.coastal.model.billing.ClientRequest;

public interface ClientRequestService {
	Integer save(ClientRequest request);
	ClientRequest get(Integer id);
	List<ClientRequest> getByClientId(Integer clientId);
}
