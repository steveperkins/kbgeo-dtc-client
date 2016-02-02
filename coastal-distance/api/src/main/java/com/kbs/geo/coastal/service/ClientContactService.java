package com.kbs.geo.coastal.service;

import java.util.List;

import com.kbs.geo.coastal.model.billing.ClientContact;

public interface ClientContactService {
	Integer save(ClientContact client);
	ClientContact get(Integer id);
	List<ClientContact> getByClientId(Integer clientId);
}
