package com.kbs.geo.coastal.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kbs.geo.coastal.dao.ClientContactDao;
import com.kbs.geo.coastal.dao.ClientDao;
import com.kbs.geo.coastal.model.billing.ClientContact;
import com.kbs.geo.coastal.service.ClientContactService;

@Service
public class ClientContactServiceImpl implements ClientContactService {

	@Autowired
	private ClientContactDao clientContactDao;
	
	@Autowired
	private ClientDao clientDao;

	@Override
	public Integer save(ClientContact client) {
		return clientContactDao.save(client);
	}

	@Override
	public ClientContact get(Integer id) {
		return populateObjectProperties(clientContactDao.get(id));
	}

	@Override
	public List<ClientContact> getByClientId(Integer clientId) {
		List<ClientContact> list = clientContactDao.getByClientId(clientId);
		for(ClientContact obj: list) {
			populateObjectProperties(obj);
		}
		return list;
	}
	
	private ClientContact populateObjectProperties(ClientContact clientContact) {
		clientContact.setClient(clientDao.get(clientContact.getClientId()));
		return clientContact;
	}

}
