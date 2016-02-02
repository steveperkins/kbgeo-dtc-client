package com.kbs.geo.coastal.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kbs.geo.coastal.dao.ClientAuthDao;
import com.kbs.geo.coastal.dao.ClientAuthIpDao;
import com.kbs.geo.coastal.model.billing.ClientAuthIp;
import com.kbs.geo.coastal.service.ClientAuthIpService;

@Service
public class ClientAuthIpServiceImpl implements ClientAuthIpService {

	@Autowired
	private ClientAuthIpDao clientAuthIpDao;
	
	@Autowired
	private ClientAuthDao clientAuthDao;

	@Override
	public Integer save(ClientAuthIp clientAuth) {
		return clientAuthIpDao.save(clientAuth);
	}

	@Override
	public ClientAuthIp get(Integer id) {
		return populateObjectProperties(clientAuthIpDao.get(id));
	}

	@Override
	public List<ClientAuthIp> getByClientId(Integer clientId) {
		List<ClientAuthIp> list = clientAuthIpDao.getByClientId(clientId);
		for(ClientAuthIp obj: list) {
			populateObjectProperties(obj);
		}
		return list;
	}
	
	@Override
	public List<ClientAuthIp> getByClientAuthId(Integer clientAuthId) {
		List<ClientAuthIp> list = clientAuthIpDao.getByClientAuthId(clientAuthId);
		for(ClientAuthIp obj: list) {
			populateObjectProperties(obj);
		}
		return list;
	}
	
	private ClientAuthIp populateObjectProperties(ClientAuthIp clientAuth) {
		clientAuth.setClientAuth(clientAuthDao.get(clientAuth.getClientAuthId()));
		return clientAuth;
	}

	

}
