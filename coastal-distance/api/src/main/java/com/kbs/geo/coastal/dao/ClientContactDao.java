package com.kbs.geo.coastal.dao;

import java.util.List;

import com.kbs.geo.coastal.model.billing.ClientContact;

public interface ClientContactDao {
	Integer save(ClientContact client);
	ClientContact get(Integer id);
	List<ClientContact> getByClientId(Integer clientId);
}
