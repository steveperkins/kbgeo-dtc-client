package com.kbs.geo.coastal.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kbs.geo.coastal.dao.ClientAuthDao;
import com.kbs.geo.coastal.dao.ClientRequestDao;
import com.kbs.geo.coastal.model.billing.ClientRequest;
import com.kbs.geo.coastal.model.billing.RequestType;
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
	
	public Long getRequestCount(Integer clientId, Date beginDate, Date endDate, RequestType requestType) {
		return clientRequestDao.getCount(clientId, beginDate, endDate, requestType);
	}
	
	public Long getRequestCount(Integer clientId, RequestType requestType) {
		return clientRequestDao.getCount(clientId, requestType);
	}
	
	private ClientRequest populateObjectProperties(ClientRequest clientRequest) {
		clientRequest.setClientAuth(clientAuthDao.get(clientRequest.getClientAuthId()));
		return clientRequest;
	}

}
