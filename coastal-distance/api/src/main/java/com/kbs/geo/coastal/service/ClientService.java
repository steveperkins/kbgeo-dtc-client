package com.kbs.geo.coastal.service;

import java.util.List;

import com.kbs.geo.coastal.model.billing.Client;

public interface ClientService {
	Integer save(Client client);
	Client get(Integer id);
	List<Client> getAllActive();
}
