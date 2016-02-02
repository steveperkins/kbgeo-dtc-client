package com.kbs.geo.coastal.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kbs.geo.coastal.dao.RequestErrorDao;
import com.kbs.geo.coastal.model.billing.RequestError;
import com.kbs.geo.coastal.service.RequestErrorService;

@Service
public class RequestErrorServiceImpl implements RequestErrorService {

	@Autowired
	private RequestErrorDao requestErrorDao;

	@Override
	public Integer save(RequestError request) {
		return requestErrorDao.save(request);
	}

	@Override
	public RequestError get(Integer id) {
		return requestErrorDao.get(id);
	}
}
