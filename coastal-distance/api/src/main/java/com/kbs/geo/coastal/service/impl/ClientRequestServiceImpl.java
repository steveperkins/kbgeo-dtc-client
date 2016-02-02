package com.kbs.geo.coastal.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kbs.geo.coastal.dao.ClientAuthDao;
import com.kbs.geo.coastal.dao.ClientRequestDao;
import com.kbs.geo.coastal.model.billing.ClientRequest;
import com.kbs.geo.coastal.service.ClientRequestService;

@Service
public class ClientRequestServiceImpl implements ClientRequestService {

	@Autowired
	private ClientRequestDao clientRequestDao;
	
	@Autowired
	private ClientAuthDao clientAuthDao;

	@Override
	public Integer save(ClientRequest client) {
		return clientRequestDao.save(client);
	}

	@Override
	public ClientRequest get(Integer id) {
		return populateObjectProperties(clientRequestDao.get(id));
	}

	@Override
	public List<ClientRequest> getByClientId(Integer clientId) {
		List<ClientRequest> list = clientRequestDao.getByClientId(clientId);
		for(ClientRequest obj: list) {
			populateObjectProperties(obj);
		}
		return list;
	}
	
	private ClientRequest populateObjectProperties(ClientRequest clientRequest) {
		clientRequest.setClientAuth(clientAuthDao.get(clientRequest.getClientAuthId()));
		return clientRequest;
	}

}
