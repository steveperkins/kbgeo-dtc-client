package com.kbs.geo.coastal.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kbs.geo.coastal.dao.ClientAuthDao;
import com.kbs.geo.coastal.dao.ClientAuthWebDao;
import com.kbs.geo.coastal.dao.ClientContactDao;
import com.kbs.geo.coastal.model.billing.ClientAuthWeb;
import com.kbs.geo.coastal.service.ClientAuthWebService;

@Service
public class ClientAuthWebServiceImpl implements ClientAuthWebService {

	@Autowired
	private ClientAuthWebDao clientAuthWebDao;
	
	@Autowired
	private ClientAuthDao clientAuthDao;
	
	@Autowired
	private ClientContactDao clientContactDao;

	@Override
	public Integer save(ClientAuthWeb clientAuth) {
		return clientAuthWebDao.save(clientAuth);
	}

	@Override
	public ClientAuthWeb get(Integer id) {
		return populateObjectProperties(clientAuthWebDao.get(id));
	}
	
	@Override
	public ClientAuthWeb getByUsername(String username) {
		return populateObjectProperties(clientAuthWebDao.getByUsername(username));
	}
	
	@Override
	public ClientAuthWeb getByUsernamePassword(String username, String encodedPassword) {
		return populateObjectProperties(clientAuthWebDao.getByUsernamePassword(username, encodedPassword));
	}
	
	@Override
	public List<ClientAuthWeb> getByClientId(Integer clientId) {
		List<ClientAuthWeb> list = clientAuthWebDao.getByClientId(clientId);
		for(ClientAuthWeb obj: list) {
			populateObjectProperties(obj);
		}
		return list;
	}
	
	@Override
	public List<ClientAuthWeb> getByClientAuthId(Integer clientAuthId) {
		List<ClientAuthWeb> list = clientAuthWebDao.getByClientAuthId(clientAuthId);
		for(ClientAuthWeb obj: list) {
			populateObjectProperties(obj);
		}
		return list;
	}
	
	@Override
	public ClientAuthWeb getByClientContactId(Integer clientContactId) {
		ClientAuthWeb obj = clientAuthWebDao.getByClientContactId(clientContactId);
		if(null != obj)
		populateObjectProperties(obj);
		return obj;
	}
	
	private ClientAuthWeb populateObjectProperties(ClientAuthWeb clientAuthWeb) {
		if(null != clientAuthWeb) {
			clientAuthWeb.setClientAuth(clientAuthDao.get(clientAuthWeb.getClientAuthId()));
			clientAuthWeb.setClientContact(clientContactDao.get(clientAuthWeb.getClientContactId()));
		}
		return clientAuthWeb;
	}

}
