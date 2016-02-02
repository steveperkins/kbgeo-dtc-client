package com.kbs.geo.coastal.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kbs.geo.coastal.dao.ClientContractDao;
import com.kbs.geo.coastal.dao.ClientDao;
import com.kbs.geo.coastal.model.billing.ClientContract;
import com.kbs.geo.coastal.service.ClientContractService;

@Service
public class ClientContractServiceImpl implements ClientContractService {

	@Autowired
	private ClientContractDao clientContractDao;
	
	@Autowired
	private ClientDao clientDao;

	@Override
	public Integer save(ClientContract contract) {
		return clientContractDao.save(contract);
	}

	@Override
	public ClientContract get(Integer id) {
		return populateObjectProperties(clientContractDao.get(id));
	}

	@Override
	public List<ClientContract> getByClientId(Integer clientId) {
		List<ClientContract> list = clientContractDao.getByClientId(clientId);
		for(ClientContract obj: list) {
			populateObjectProperties(obj);
		}
		return list;
	}
	
	private ClientContract populateObjectProperties(ClientContract contract) {
		contract.setClient(clientDao.get(contract.getClientId()));
		return contract;
	}

}
