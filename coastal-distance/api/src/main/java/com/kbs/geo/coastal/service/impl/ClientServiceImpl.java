package com.kbs.geo.coastal.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kbs.geo.coastal.dao.ClientDao;
import com.kbs.geo.coastal.model.billing.Client;
import com.kbs.geo.coastal.service.ClientService;

@Service
public class ClientServiceImpl implements ClientService {

	@Autowired
	private ClientDao clientDao;

	@Override
	public Integer save(Client client) {
		return clientDao.save(client);
	}

	@Override
	public Client get(Integer id) {
		return clientDao.get(id);
	}

	@Override
	public List<Client> getAllActive() {
		return clientDao.getAllActive(Boolean.TRUE);
	}

}
