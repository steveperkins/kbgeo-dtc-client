package com.kbs.geo.coastal.dao;

import java.util.List;

import com.kbs.geo.coastal.model.billing.Client;

public interface ClientDao {
	Integer save(Client client);
	Client get(Integer id);
	List<Client> getAllActive(Boolean active);
}
