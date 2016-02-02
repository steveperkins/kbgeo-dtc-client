package com.kbs.geo.coastal.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kbs.geo.coastal.dao.ClientAuthDao;
import com.kbs.geo.coastal.dao.ClientDao;
import com.kbs.geo.coastal.model.billing.ClientAuth;
import com.kbs.geo.coastal.service.ClientAuthService;

@Service
public class ClientAuthServiceImpl implements ClientAuthService {

	@Autowired
	private ClientAuthDao clientAuthDao;
	
	@Autowired
	private ClientDao clientDao;

	@Override
	public Integer save(ClientAuth clientAuth) {
		return clientAuthDao.save(clientAuth);
	}

	@Override
	public ClientAuth get(Integer id) {
		return populateObjectProperties(clientAuthDao.get(id));
	}

	@Override
	public List<ClientAuth> getByClientId(Integer clientId) {
		List<ClientAuth> list = clientAuthDao.getByClientId(clientId);
		for(ClientAuth obj: list) {
			populateObjectProperties(obj);
		}
		return list;
	}
	
	@Override
	public ClientAuth getByToken(String token) {
		return populateObjectProperties(clientAuthDao.getByToken(token));
	}
	
	private ClientAuth populateObjectProperties(ClientAuth clientAuth) {
		if(null == clientAuth) return null;
		clientAuth.setClient(clientDao.get(clientAuth.getClientId()));
		return clientAuth;
	}


}
