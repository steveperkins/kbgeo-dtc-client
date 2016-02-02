package com.kbs.geo.coastal.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kbs.geo.coastal.dao.ClientAuthDao;
import com.kbs.geo.coastal.dao.ClientAuthRefererDao;
import com.kbs.geo.coastal.model.billing.ClientAuthReferer;
import com.kbs.geo.coastal.service.ClientAuthRefererService;

@Service
public class ClientAuthRefererServiceImpl implements ClientAuthRefererService {

	@Autowired
	private ClientAuthRefererDao clientAuthRefererDao;
	
	@Autowired
	private ClientAuthDao clientAuthDao;

	@Override
	public Integer save(ClientAuthReferer clientAuth) {
		return clientAuthRefererDao.save(clientAuth);
	}

	@Override
	public ClientAuthReferer get(Integer id) {
		return populateObjectProperties(clientAuthRefererDao.get(id));
	}

	@Override
	public List<ClientAuthReferer> getByClientId(Integer clientId) {
		List<ClientAuthReferer> list = clientAuthRefererDao.getByClientId(clientId);
		for(ClientAuthReferer obj: list) {
			populateObjectProperties(obj);
		}
		return list;
	}
	
	@Override
	public List<ClientAuthReferer> getByClientAuthId(Integer clientAuthId) {
		List<ClientAuthReferer> list = clientAuthRefererDao.getByClientAuthId(clientAuthId);
		for(ClientAuthReferer obj: list) {
			populateObjectProperties(obj);
		}
		return list;
	}
	
	private ClientAuthReferer populateObjectProperties(ClientAuthReferer clientAuth) {
		clientAuth.setClientAuth(clientAuthDao.get(clientAuth.getClientAuthId()));
		return clientAuth;
	}

	

}
