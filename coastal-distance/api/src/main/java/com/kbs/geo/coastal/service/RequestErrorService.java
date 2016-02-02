package com.kbs.geo.coastal.service;

import com.kbs.geo.coastal.model.billing.RequestError;

public interface RequestErrorService {
	Integer save(RequestError request);
	RequestError get(Integer id);
}
